package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.view.ui.components.RecipeContentDialog
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavRecipesPage(dao: RecipeDao) {
    val recipes = remember {
        dao.getRecipes().toMutableStateList()
    }

    val recipeQuery = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold() {
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            DefaultTextField(label = "Search Recipes", value = recipeQuery)

            if (recipes.isEmpty()) {
                Text("No Favorites", color = BlueIsh, fontSize = 20.sp)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(it),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(recipes.size) { index ->
                        val recipe = recipes[index]
                        RecipeBox(
                            dao = dao,
                            recipe = recipe,
                            favRecipes = recipes
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeBox(
    dao: RecipeDao,
    recipe: Recipe,
    favRecipes: SnapshotStateList<Recipe>,
    modifier: Modifier = Modifier
) {
    val isShowingRecipe = remember {
        mutableStateOf(false)
    }

    val isToBeDeleted = remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    RecipeContentDialog(
        dao = dao,
        recipe = recipe,
        isShowing = isShowingRecipe,
    ) {
        scope.launch {
            isToBeDeleted.value = true
            delay(300)
            isToBeDeleted.value = false
            favRecipes.remove(recipe)
        }
    }

    AnimatedVisibility(visible = !isToBeDeleted.value) {
        Box(modifier = modifier
            .padding(16.dp)
            .clickable {
                isShowingRecipe.value = !isShowingRecipe.value
            }) {
            Text(
                text = recipe.name,
                color = BlueIsh,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}