package jr.brian.issarecipeapp.view.ui.pages

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.AppDataStore
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.remote.ApiService
import jr.brian.issarecipeapp.util.API_KEY_REQUIRED
import jr.brian.issarecipeapp.util.DIETARY_RESTRICTIONS_LABEL
import jr.brian.issarecipeapp.util.ERROR_IMAGE_URL
import jr.brian.issarecipeapp.util.FOOD_ALLERGY_LABEL
import jr.brian.issarecipeapp.util.INGREDIENTS_LABEL
import jr.brian.issarecipeapp.util.NO_RESPONSE_MSG
import jr.brian.issarecipeapp.util.PARTY_SIZE_LABEL
import jr.brian.issarecipeapp.util.PARTY_SIZE_MAX_CHAR_COUNT
import jr.brian.issarecipeapp.util.allergyOptions
import jr.brian.issarecipeapp.util.customTextSelectionColors
import jr.brian.issarecipeapp.util.dietaryOptions
import jr.brian.issarecipeapp.util.generateRecipeQuery
import jr.brian.issarecipeapp.util.ifBlankUse
import jr.brian.issarecipeapp.util.isUrl
import jr.brian.issarecipeapp.util.occasionOptions
import jr.brian.issarecipeapp.util.randomInfo
import jr.brian.issarecipeapp.util.randomMealOccasion
import jr.brian.issarecipeapp.util.showToast
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
import jr.brian.issarecipeapp.view.ui.components.LottieRecipe
import jr.brian.issarecipeapp.view.ui.components.OptionsDialog
import jr.brian.issarecipeapp.view.ui.components.RecipeNameDialog
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.Crimson
import jr.brian.issarecipeapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun GenerateRecipePage(
    dao: RecipeDao,
    dataStore: AppDataStore,
    dietaryRestrictions: String,
    foodAllergies: String,
    gptModel: String,
    isImageGenerationEnabled: String,
    viewModel: MainViewModel = hiltViewModel(),
    onNavToSettings: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val mealType = remember { mutableStateOf("") }
    val servingSize = remember { mutableStateOf("") }

    val dietary = remember {
        mutableStateOf(dietaryRestrictions)
    }

    val allergies = remember {
        mutableStateOf(foodAllergies)
    }

    val model = remember {
        mutableStateOf(gptModel)
    }

    val isImageGenEnabled = remember {
        mutableStateOf(isImageGenerationEnabled)
    }

    val ingredients = remember { mutableStateOf("") }
    val additionalInfo = remember { mutableStateOf("") }

    val generatedRecipe = remember {
        mutableStateOf("")
    }

    val hasBeenSaved = remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    val loading = viewModel.loading.collectAsState()
    val imageLoading = viewModel.imageLoading.collectAsState()
    val imageUrl = viewModel.imageUrlResponse.collectAsState()
    val recipeTitle = viewModel.recipeTitle.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    val pagerState = rememberPagerState()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                focusManager.clearFocus()
                if (pagerState.currentPage == 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                } else {
                    isEnabled = false
                    backPressedDispatcher?.onBackPressed()
                }
            }
        }
    }

    val onGenNewImage = fun(includeIngredients: Boolean) {
        if (isImageGenEnabled.value.toBoolean()) {
            scope.launch {
                viewModel.recipeTitle.value?.let { title ->
                    viewModel.generateImageUrl(
                        title = title,
                        ingredients = if (includeIngredients) ingredients.value else null
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        backPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { currentPageIndex ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(15.dp))

                    when (currentPageIndex) {
                        0 -> {
                            MealDetails(
                                dataStore = dataStore,
                                occasion = mealType,
                                partySize = servingSize,
                                dietaryRestrictions = dietary,
                                foodAllergies = allergies,
                                gptModel = model,
                                ingredients = ingredients,
                                additionalInfo = additionalInfo,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope,
                                viewModel = viewModel,
                                loading = loading,
                                onNavToSettings = onNavToSettings,
                                focusManager = focusManager,
                                onGenerateNewImage = { includeIngredients ->
                                    onGenNewImage(includeIngredients)
                                }
                            )
                        }

                        1 -> {
                            RecipeResults(
                                dao = dao,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope,
                                loading = loading,
                                imageLoading = imageLoading,
                                imageUrl = imageUrl,
                                recipeTitle = recipeTitle,
                                onGenerateNewImage = { includeIngredients ->
                                    onGenNewImage(includeIngredients)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = BlueIsh,
                inactiveColor = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MealDetails(
    dataStore: AppDataStore,
    occasion: MutableState<String>,
    partySize: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    gptModel: MutableState<String>,
    ingredients: MutableState<String>,
    additionalInfo: MutableState<String>,
    generatedRecipe: MutableState<String>,
    hasBeenSaved: MutableState<Boolean>,
    pagerState: PagerState,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    loading: State<Boolean>,
    focusManager: FocusManager,
    onNavToSettings: () -> Unit,
    onGenerateNewImage: (includeIngredients: Boolean) -> Unit,
) {
    val context = LocalContext.current

    val showErrorColorIngredients = remember {
        mutableStateOf(false)
    }

    val isOccasionOptionsShowing = remember {
        mutableStateOf(false)
    }

    val isDietaryOptionsShowing = remember {
        mutableStateOf(false)
    }

    val isAllergyOptionsShowing = remember {
        mutableStateOf(false)
    }

    val isGenerateBtnShowing = remember {
        mutableStateOf(true)
    }

    val isRandomBtnShowing = remember {
        mutableStateOf(true)
    }

    val isPartySizeFocused = remember { mutableStateOf(false) }
    val isIngredientsFocused = remember { mutableStateOf(false) }
    val isOccasionFocused = remember { mutableStateOf(false) }
    val isDietaryFocused = remember { mutableStateOf(false) }
    val isAllergiesFocused = remember { mutableStateOf(false) }
    val isOtherFocused = remember { mutableStateOf(false) }
    val isTapInfoShowing = remember { mutableStateOf(false) }

    OptionsDialog(
        isShowing = isOccasionOptionsShowing,
        title = "Occasions",
        options = occasionOptions,
        onSelectItem = {
            occasion.value = it
        })

    OptionsDialog(
        isShowing = isDietaryOptionsShowing,
        title = "Restrictions",
        options = dietaryOptions,
        onSelectItem = {
            dietaryRestrictions.value = it
            scope.launch {
                dataStore.saveDietaryRestrictions(it)
            }
        })

    OptionsDialog(
        isShowing = isAllergyOptionsShowing,
        title = "Allergies",
        options = allergyOptions,
        onSelectItem = {
            foodAllergies.value = it
            scope.launch {
                dataStore.saveFoodAllergies(it)
            }
        })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AnimatedVisibility(
                visible =
                !isIngredientsFocused.value &&
                        !isOccasionFocused.value &&
                        !isDietaryFocused.value &&
                        !isAllergiesFocused.value &&
                        !isOtherFocused.value
            ) {
                DefaultTextField(
                    label = PARTY_SIZE_LABEL,
                    value = partySize.value,
                    onValueChange = {
                        partySize.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isPartySizeFocused.value = it.isFocused
                        },
                    maxCount = PARTY_SIZE_MAX_CHAR_COUNT,
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            AnimatedVisibility(
                visible =
                !isPartySizeFocused.value &&
                        !isOccasionFocused.value &&
                        !isDietaryFocused.value &&
                        !isAllergiesFocused.value &&
                        !isOtherFocused.value
            ) {
                DefaultTextField(
                    label = INGREDIENTS_LABEL,
                    value = ingredients.value,
                    onValueChange = {
                        ingredients.value = it
                    },
                    isError = showErrorColorIngredients,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isIngredientsFocused.value = it.isFocused
                        },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            AnimatedVisibility(
                visible =
                !isIngredientsFocused.value &&
                        !isPartySizeFocused.value &&
                        !isDietaryFocused.value &&
                        !isAllergiesFocused.value &&
                        !isOtherFocused.value
            ) {
                DefaultTextField(
                    label = "Occasion | Ex: $randomMealOccasion",
                    value = occasion.value,
                    onValueChange = {
                        occasion.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isOccasionFocused.value = it.isFocused
                        },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            tint = BlueIsh,
                            contentDescription = "View preset occasion options",
                            modifier = Modifier.clickable {
                                focusManager.clearFocus()
                                isDietaryOptionsShowing.value = false
                                isAllergyOptionsShowing.value = false
                                isOccasionOptionsShowing.value = !isOccasionOptionsShowing.value
                            }
                        )
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            AnimatedVisibility(
                visible =
                !isIngredientsFocused.value &&
                        !isOccasionFocused.value &&
                        !isPartySizeFocused.value &&
                        !isAllergiesFocused.value &&
                        !isOtherFocused.value
            ) {
                DefaultTextField(
                    label = DIETARY_RESTRICTIONS_LABEL,
                    value = dietaryRestrictions.value,
                    onValueChange = {
                        dietaryRestrictions.value = it
                        scope.launch {
                            dataStore.saveDietaryRestrictions(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isDietaryFocused.value = it.isFocused
                        },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            tint = BlueIsh,
                            contentDescription = "View preset dietary restrictions",
                            modifier = Modifier.clickable {
                                isOccasionOptionsShowing.value = false
                                isAllergyOptionsShowing.value = false
                                isDietaryOptionsShowing.value = !isDietaryOptionsShowing.value
                            }
                        )
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            AnimatedVisibility(
                visible =
                !isIngredientsFocused.value &&
                        !isOccasionFocused.value &&
                        !isDietaryFocused.value &&
                        !isPartySizeFocused.value &&
                        !isOtherFocused.value
            ) {
                DefaultTextField(
                    label = FOOD_ALLERGY_LABEL,
                    value = foodAllergies.value,
                    onValueChange = {
                        foodAllergies.value = it
                        scope.launch {
                            dataStore.saveFoodAllergies(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isAllergiesFocused.value = it.isFocused
                        },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            tint = BlueIsh,
                            contentDescription = "View preset food allergies",
                            modifier = Modifier.clickable {
                                isDietaryOptionsShowing.value = false
                                isOccasionOptionsShowing.value = false
                                isAllergyOptionsShowing.value = !isAllergyOptionsShowing.value
                            }
                        )
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            AnimatedVisibility(
                visible =
                !isIngredientsFocused.value &&
                        !isOccasionFocused.value &&
                        !isDietaryFocused.value &&
                        !isAllergiesFocused.value &&
                        !isPartySizeFocused.value
            ) {
                DefaultTextField(
                    label = "Other Info | Ex: $randomInfo",
                    value = additionalInfo.value,
                    onValueChange = {
                        additionalInfo.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isOtherFocused.value = it.isFocused
                        },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        end = 15.dp
                    ),
                onClick = {
                    focusManager.clearFocus()
                    if (ApiService.ApiKey.userApiKey.isBlank()) {
                        context.showToast(API_KEY_REQUIRED)
                        onNavToSettings()
                    } else if (ingredients.value.isBlank()) {
                        showErrorColorIngredients.value = true
                    } else if (!loading.value) {
                        showErrorColorIngredients.value = false

                        if (partySize.value.toIntOrNull() == null) {
                            partySize.value = "1"
                        }

                        scope.launch {
                            delay(300)
                            isRandomBtnShowing.value = false
                        }

                        occasion.value = occasion.value.ifBlankUse("any occasion")
                        dietaryRestrictions.value =
                            dietaryRestrictions.value.ifBlankUse("none")
                        foodAllergies.value = foodAllergies.value.ifBlankUse("none")

                        val query = generateRecipeQuery(
                            occasion = occasion.value,
                            partySize = partySize.value,
                            dietaryRestrictions = dietaryRestrictions.value,
                            foodAllergies = foodAllergies.value,
                            ingredients = ingredients.value,
                            additionalInfo = additionalInfo.value,
                        )

                        focusManager.clearFocus()

                        scope.launch {
                            pagerState.animateScrollToPage(1)
                            generatedRecipe.value = ""
                            viewModel.getAskResponse(
                                userPrompt = query,
                                model = gptModel.value
                            )
                            onGenerateNewImage(false)
                            generatedRecipe.value =
                                viewModel.response.value ?: NO_RESPONSE_MSG
                        }

                        hasBeenSaved.value = false
                    }
                }) {

                if (loading.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Generate Recipe")
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = isRandomBtnShowing.value) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 15.dp,
                            end = 15.dp
                        ),
                    onClick = {
                        if (ApiService.ApiKey.userApiKey.isBlank()) {
                            context.showToast(API_KEY_REQUIRED)
                            onNavToSettings()
                        } else if (!loading.value) {
                            scope.launch {
                                delay(300)
                                isTapInfoShowing.value = false
                                isGenerateBtnShowing.value = false
                            }

                            occasion.value = "any occasion"
                            partySize.value = "1"
                            dietaryRestrictions.value = dietaryRestrictions.value.ifBlank { "none" }
                            foodAllergies.value = foodAllergies.value.ifBlank { "none" }
                            ingredients.value = "use random ingredients"
                            additionalInfo.value = "provide random recipe"

                            val query = generateRecipeQuery(
                                occasion = occasion.value,
                                partySize = partySize.value,
                                dietaryRestrictions = dietaryRestrictions.value,
                                foodAllergies = foodAllergies.value,
                                ingredients = ingredients.value,
                                additionalInfo = additionalInfo.value,
                            )

                            showErrorColorIngredients.value = false

                            focusManager.clearFocus()

                            scope.launch {
                                pagerState.animateScrollToPage(1)
                                generatedRecipe.value = ""
                                viewModel.getAskResponse(
                                    userPrompt = query,
                                    model = gptModel.value
                                )
                                onGenerateNewImage(false)
                                generatedRecipe.value =
                                    viewModel.response.value ?: NO_RESPONSE_MSG
                            }

                            hasBeenSaved.value = false
                        }
                    }) {

                    if (loading.value) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Random Recipe")
                    }
                }
            }

            AnimatedVisibility(
                visible = isTapInfoShowing.value ||
                        isIngredientsFocused.value ||
                        isOccasionFocused.value ||
                        isDietaryFocused.value ||
                        isAllergiesFocused.value ||
                        isPartySizeFocused.value ||
                        isOtherFocused.value
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_info_24),
                        contentDescription = "Tap Info",
                        tint = BlueIsh
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "Tap anywhere to show all fields")
                }
            }
        }
    }
}

@OptIn(
    ExperimentalPagerApi::class, ExperimentalGlideComposeApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun RecipeResults(
    dao: RecipeDao,
    generatedRecipe: MutableState<String>,
    hasBeenSaved: MutableState<Boolean>,
    pagerState: PagerState,
    scope: CoroutineScope,
    loading: State<Boolean>,
    imageLoading: State<Boolean>,
    imageUrl: State<String?>,
    recipeTitle: State<String?>,
    onGenerateNewImage: (Boolean) -> Unit
) {
    val isShowingSavedSuccess = remember {
        mutableStateOf(false)
    }


    val isShowingSaveNameDialog = remember {
        mutableStateOf(false)
    }

    val isShowingSaveNameError = remember {
        mutableStateOf(false)
    }

    val name = remember {
        mutableStateOf("")
    }

    RecipeNameDialog(
        isShowing = isShowingSaveNameDialog,
        name = name,
        onConfirmClick = {
            if (it.isNotBlank()) {
                dao.insertRecipe(
                    Recipe(
                        recipe = generatedRecipe.value,
                        name = it,
                        imageUrl = imageUrl.value.orEmpty()
                    )
                )
                isShowingSavedSuccess.value = true
                isShowingSaveNameDialog.value = false
                name.value = ""
                scope.launch {
                    delay(1000)
                    isShowingSavedSuccess.value = false
                    delay(500)
                    hasBeenSaved.value = true
                }
            } else {
                isShowingSaveNameError.value = true
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }) {
                if (loading.value) {
                    Text("Generating Recipe")
                } else {
                    Text(if (imageLoading.value) "Generating Image" else "Back")
                }
            }

            AnimatedVisibility(
                generatedRecipe.value.isNotBlank()
                        && !hasBeenSaved.value
                        && !loading.value
            ) {
                IconButton(
                    modifier = Modifier.padding(start = 30.dp),
                    onClick = {
                        if (name.value.isBlank()) {
                            name.value = recipeTitle.value.orEmpty()
                        }
                        isShowingSaveNameDialog.value = !isShowingSaveNameDialog.value
                    }) {
                    Icon(
                        tint = Crimson,
                        modifier = Modifier.size(50.dp),
                        painter = painterResource(id = R.drawable.baseline_favorite_24),
                        contentDescription = "Favorite this recipe"
                    )
                }
            }

            AnimatedVisibility(visible = isShowingSavedSuccess.value) {
                Text(
                    text = "Saved!",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        }

        AnimatedVisibility(visible = imageLoading.value) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "The recipe image may take longer to display.",
                    fontSize = 16.sp
                )
            }
        }

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(1) {
                CompositionLocalProvider(
                    LocalTextSelectionColors provides customTextSelectionColors
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedVisibility(visible = imageLoading.value || loading.value) {
                        LottieRecipe(
                            isShowing = remember {
                                mutableStateOf(true)
                            }, modifier = Modifier.size(250.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = !imageLoading.value
                                && !loading.value
                                && imageUrl.value != null
                    ) {
                        val isImageUrlValid = imageUrl.value?.isUrl() ?: false
                        GlideImage(
                            model = if (isImageUrlValid) imageUrl.value else ERROR_IMAGE_URL,
                            contentDescription = "Recipe Image",
                            loading = placeholder(ColorPainter(Color.Gray)),
                            modifier = if (isImageUrlValid) {
                                Modifier.combinedClickable(
                                    onClick = {},
                                    onDoubleClick = {
                                        onGenerateNewImage(true)
                                    })
                            } else Modifier
                        )

                    }

                    SelectionContainer {
                        Text(
                            generatedRecipe.value,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }
        }
    }
}