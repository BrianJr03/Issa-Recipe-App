package jr.brian.issarecipeapp.view.ui.pages

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.DIETARY_RESTRICTIONS_LABEL
import jr.brian.issarecipeapp.util.FOOD_ALLERGY_LABEL
import jr.brian.issarecipeapp.util.INGREDIENTS_LABEL
import jr.brian.issarecipeapp.util.PARTY_SIZE_LABEL
import jr.brian.issarecipeapp.util.PARTY_SIZE_MAX_CHAR_COUNT
import jr.brian.issarecipeapp.util.customTextSelectionColors
import jr.brian.issarecipeapp.util.generateRecipeQuery
import jr.brian.issarecipeapp.util.ifBlankUse
import jr.brian.issarecipeapp.util.randomInfo
import jr.brian.issarecipeapp.util.randomMealOccasion
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
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
    viewModel: MainViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val mealType = remember { mutableStateOf("") }
    val servingSize = remember { mutableStateOf("") }
    val dietaryRestrictions = remember { mutableStateOf("") }
    val foodAllergies = remember { mutableStateOf("") }
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

    val interactionSource = remember { MutableInteractionSource() }

    val pagerState = rememberPagerState()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
                                occasion = mealType,
                                partySize = servingSize,
                                dietaryRestrictions = dietaryRestrictions,
                                foodAllergies = foodAllergies,
                                ingredients = ingredients,
                                additionalInfo = additionalInfo,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope,
                                viewModel = viewModel,
                                loading = loading
                            )
                        }

                        1 -> {
                            RecipeResults(
                                dao = dao,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope,
                                loading = loading
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
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MealDetails(
    occasion: MutableState<String>,
    partySize: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    ingredients: MutableState<String>,
    additionalInfo: MutableState<String>,
    generatedRecipe: MutableState<String>,
    hasBeenSaved: MutableState<Boolean>,
    pagerState: PagerState,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    loading: State<Boolean>
) {
    val focusManager = LocalFocusManager.current

    val showErrorColorIngredients = remember {
        mutableStateOf(false)
    }

    val showErrorColorPartySize = remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(1) {
            DefaultTextField(
                label = PARTY_SIZE_LABEL,
                value = partySize,
                modifier = Modifier
                    .fillMaxWidth(),
                maxCount = PARTY_SIZE_MAX_CHAR_COUNT,
                isShowingErrorColor = showErrorColorPartySize
            )

            DefaultTextField(
                label = INGREDIENTS_LABEL,
                value = ingredients,
                modifier = Modifier
                    .fillMaxWidth(),
                isShowingErrorColor = showErrorColorIngredients
            )

            DefaultTextField(
                label = "Occasion | Ex: $randomMealOccasion",
                value = occasion,
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        tint = BlueIsh,
                        contentDescription = "View preset choices",
                        modifier = Modifier.clickable {

                        }
                    )
                }
            )

            DefaultTextField(
                label = DIETARY_RESTRICTIONS_LABEL,
                value = dietaryRestrictions,
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        tint = BlueIsh,
                        contentDescription = "View preset choices",
                        modifier = Modifier.clickable {

                        }
                    )
                }

            )

            DefaultTextField(
                label = FOOD_ALLERGY_LABEL,
                value = foodAllergies,
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        tint = BlueIsh,
                        contentDescription = "View preset choices",
                        modifier = Modifier.clickable {

                        }
                    )
                }
            )

            DefaultTextField(
                label = "Other Info | Ex: $randomInfo",
                value = additionalInfo,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        end = 15.dp
                    ),
                onClick = {
                    if (partySize.value.toIntOrNull() == null) {
                        showErrorColorPartySize.value = true
                    } else if (ingredients.value.isBlank()) {
                        showErrorColorIngredients.value = true
                    } else if (!loading.value) {
                        occasion.value = occasion.value.ifBlankUse("any occasion")
                        dietaryRestrictions.value =
                            dietaryRestrictions.value.ifBlankUse("none")
                        foodAllergies.value = foodAllergies.value.ifBlankUse("none")

                        val query = generateRecipeQuery(
                            occasion = occasion,
                            partySize = partySize,
                            dietaryRestrictions = dietaryRestrictions,
                            foodAllergies = foodAllergies,
                            ingredients = ingredients,
                            additionalInfo = additionalInfo,
                        )

                        focusManager.clearFocus()

                        scope.launch {
                            generatedRecipe.value = ""
                            viewModel.getChefGptResponse(userPrompt = query)
                            generatedRecipe.value =
                                viewModel.response.value ?: "Empty Response. Please try again."
                            pagerState.animateScrollToPage(1)
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

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        end = 15.dp
                    ),
                onClick = {
                    if (!loading.value) {
                        occasion.value = "any occasion"
                        partySize.value = "1"
                        dietaryRestrictions.value = "none"
                        foodAllergies.value = "none"
                        ingredients.value = "use random ingredients"
                        additionalInfo.value = "provide random recipe"

                        val query = generateRecipeQuery(
                            occasion = occasion,
                            partySize = partySize,
                            dietaryRestrictions = dietaryRestrictions,
                            foodAllergies = foodAllergies,
                            ingredients = ingredients,
                            additionalInfo = additionalInfo,
                        )

                        showErrorColorPartySize.value = false
                        showErrorColorIngredients.value = false

                        focusManager.clearFocus()

                        scope.launch {
                            generatedRecipe.value = ""
                            viewModel.getChefGptResponse(userPrompt = query)
                            generatedRecipe.value =
                                viewModel.response.value ?: "Empty Response. Please try again."
                            pagerState.animateScrollToPage(1)
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
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeResults(
    dao: RecipeDao,
    generatedRecipe: MutableState<String>,
    hasBeenSaved: MutableState<Boolean>,
    pagerState: PagerState,
    scope: CoroutineScope,
    loading: State<Boolean>
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
        isShowingErrorColor = isShowingSaveNameError
    ) {
        if (name.value.isNotBlank()) {
            dao.insertRecipe(Recipe(generatedRecipe.value, name.value))
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
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Back")
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
                    color = BlueIsh,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        }

        LazyColumn {
            items(1) {
                CompositionLocalProvider(
                    LocalTextSelectionColors provides customTextSelectionColors
                ) {
                    SelectionContainer {
                        Text(
                            generatedRecipe.value,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }

                if (generatedRecipe.value.isBlank()) {
                    Text(
                        "No Generated Recipe",
                        fontSize = 20.sp,
                        color = BlueIsh
                    )
                }
            }
        }
    }
}