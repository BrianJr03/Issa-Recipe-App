package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MealDetailPage(mealName: String) {
    val scope = rememberCoroutineScope()
    val servingSize = remember { mutableStateOf("") }
    val dietaryRestrictions = remember { mutableStateOf("") }
    val foodAllergies = remember { mutableStateOf("") }
    val ingredients = remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val interactionSource = remember { MutableInteractionSource() }

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

            val pagerState = rememberPagerState()

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
                                mealName = mealName,
                                partySize = servingSize,
                                dietaryRestrictions = dietaryRestrictions,
                                foodAllergies = foodAllergies,
                                ingredients = ingredients,
                                pagerState = pagerState,
                                scope = scope
                            )
                        }

                        1 -> {
                            RecipeResults(
                                mealName = mealName,
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

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun MealDetails(
    mealName: String,
    partySize: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    ingredients: MutableState<String>,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(1) {
            Text(text = "$mealName Details")

            Spacer(modifier = Modifier.height(20.dp))

            DetailTextField(
                label = "Party Size",
                value = partySize,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            DetailTextField(
                label = "Dietary Restrictions",
                value = dietaryRestrictions,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            DetailTextField(
                label = "Food Allergies",
                value = foodAllergies,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            DetailTextField(
                label = "Ingredients",
                value = ingredients,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier.padding(end = 15.dp),
                onClick = {
                    scope.launch {
                        val query =
                            "Generate a recipe for $mealName that serves ${partySize.value} " +
                                    "using the following ingredients: ${ingredients.value}. " +
                                    "Keep in mind the following " +
                                    "dietary restrictions: ${dietaryRestrictions.value}. " +
                                    "Also note that I am allergic to ${foodAllergies.value}."
                        pagerState.animateScrollToPage(1)
                    }
                }) {
                Text("Generate Recipe")
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeResults(
    mealName: String,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "$mealName Results",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Recipe",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        }) {
            Text("Back")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTextField(
    label: String,
    value: MutableState<String>,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        value = value.value,
        onValueChange = {
            value.value = it
        },
        label = {
            Text(
                text = label,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.background
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {}),
    )
}