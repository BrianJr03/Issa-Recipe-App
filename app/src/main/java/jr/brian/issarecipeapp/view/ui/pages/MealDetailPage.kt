package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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

    Scaffold() {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
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
                                servingSize = servingSize,
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

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MealDetails(
    mealName: String,
    servingSize: MutableState<String>,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$mealName Details")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = servingSize.value,
            onValueChange = {
                servingSize.value = it
            },
            label = {
                Text(
                    text = "Serving Size",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {})
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            scope.launch {
                pagerState.animateScrollToPage(1)
            }
        }) {
            Text("Generate")
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