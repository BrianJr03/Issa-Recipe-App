package jr.brian.issarecipeapp.view.ui.pages

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import jr.brian.issarecipeapp.util.MealType
import jr.brian.issarecipeapp.util.ifBlankUse
import jr.brian.issarecipeapp.view.ui.components.ShowNameRecipeDialog
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.Crimson
import jr.brian.issarecipeapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MealDetailPage(
    dao: RecipeDao,
    viewModel: MainViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val mealType = remember { mutableStateOf("") }
    val servingSize = remember { mutableStateOf("") }
    val dietaryRestrictions = remember { mutableStateOf("") }
    val foodAllergies = remember { mutableStateOf("") }
    val ingredients = remember { mutableStateOf("") }

    val generatedRecipe = remember {
        mutableStateOf("")
    }

    val hasBeenSaved = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

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

    Scaffold() {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { currentPageIndex ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (currentPageIndex) {
                        0 -> {
                            MealDetails(
                                mealType = mealType,
                                partySize = servingSize,
                                dietaryRestrictions = dietaryRestrictions,
                                foodAllergies = foodAllergies,
                                ingredients = ingredients,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope,
                                context = context,
                                viewModel = viewModel
                            )
                        }

                        1 -> {
                            RecipeResults(
                                dao = dao,
                                generatedRecipe = generatedRecipe,
                                hasBeenSaved = hasBeenSaved,
                                pagerState = pagerState,
                                scope = scope
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
    mealType: MutableState<String>,
    partySize: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    ingredients: MutableState<String>,
    generatedRecipe: MutableState<String>,
    hasBeenSaved: MutableState<Boolean>,
    pagerState: PagerState,
    scope: CoroutineScope,
    context: Context,
    viewModel: MainViewModel
) {
    val loading = viewModel.loading.collectAsState()

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
            DetailTextField(
                label = "Meal Type | ex: '${MealType.randomMealType}'",
                value = mealType,
                modifier = Modifier
                    .fillMaxWidth()
            )

            DetailTextField(
                label = "Party Size *",
                value = partySize,
                modifier = Modifier
                    .fillMaxWidth(),
                isShowingErrorColor = showErrorColorPartySize
            )

            DetailTextField(
                label = "Dietary Restrictions",
                value = dietaryRestrictions,
                modifier = Modifier
                    .fillMaxWidth()

            )

            DetailTextField(
                label = "Food Allergies",
                value = foodAllergies,
                modifier = Modifier
                    .fillMaxWidth()
            )

            DetailTextField(
                label = "Ingredients *",
                value = ingredients,
                modifier = Modifier
                    .fillMaxWidth(),
                isShowingErrorColor = showErrorColorIngredients
            )

            Button(
                modifier = Modifier.padding(end = 15.dp),
                onClick = {
                    if (partySize.value.toIntOrNull() == null) {
                        showErrorColorPartySize.value = true
                    } else if (ingredients.value.isBlank()) {
                        showErrorColorIngredients.value = true
                    } else if (!loading.value) {
                        mealType.value = mealType.value.ifBlankUse("any occasion")
                        dietaryRestrictions.value = dietaryRestrictions.value.ifBlankUse("none")
                        foodAllergies.value = foodAllergies.value.ifBlankUse("none")
                        val query =
                            "Generate a recipe for ${mealType.value} that serves ${partySize.value} " +
                                    "using the following ingredients: ${ingredients.value}. " +
                                    "Keep in mind the following " +
                                    "dietary restrictions: ${dietaryRestrictions.value}. " +
                                    "Also note that I am allergic to ${foodAllergies.value}."
                        scope.launch {
                            viewModel.getChatGptResponse(context = context, userPrompt = query)
                            generatedRecipe.value = viewModel.response.value ?: ""
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
    scope: CoroutineScope
) {
    val isShowingSavedSuccess = remember {
        mutableStateOf(false)
    }

    val copyColor = BlueIsh
    val customTextSelectionColors = TextSelectionColors(
        handleColor = copyColor,
        backgroundColor = copyColor
    )

    val isShowingSaveNameDialog = remember {
        mutableStateOf(false)
    }

    val isShowingSaveNameError = remember {
        mutableStateOf(false)
    }

    val name = remember {
        mutableStateOf("")
    }

    ShowNameRecipeDialog(
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
                Text("Back")
            }

            if (generatedRecipe.value.isNotBlank() && !hasBeenSaved.value) {
                Spacer(modifier = Modifier.width(50.dp))
                IconButton(onClick = {
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

        Spacer(modifier = Modifier.width(10.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                if (generatedRecipe.value.isBlank()) {
                    Text("No Generated Recipe", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTextField(
    label: String,
    value: MutableState<String>,
    modifier: Modifier = Modifier,
    isShowingErrorColor: MutableState<Boolean>? = null,
) {
    OutlinedTextField(
        modifier = modifier.padding(15.dp),
        value = value.value,
        onValueChange = {
            value.value = it
            if (it.isNotBlank()) {
                isShowingErrorColor?.value = false
            } else if (it.toIntOrNull() != null) {
                isShowingErrorColor?.value = false
            }
        },
        label = {
            Text(
                text = label,
                style = TextStyle(
                    color = BlueIsh,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = if (isShowingErrorColor?.value == true) Color.Red else BlueIsh,
            unfocusedIndicatorColor = if (isShowingErrorColor?.value == true) Color.Red
            else MaterialTheme.colorScheme.background
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {}),
    )
}