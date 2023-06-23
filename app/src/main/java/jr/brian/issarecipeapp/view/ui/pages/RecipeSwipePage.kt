package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.view.ui.components.swipe_cards.RecipeCard
import jr.brian.issarecipeapp.view.ui.components.swipe_cards.RecipeStack
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.dp_20
import jr.brian.issarecipeapp.view.ui.theme.sp_16
import jr.brian.issarecipeapp.viewmodel.MainViewModel

@Composable
fun RecipeSwipe(
    dao: RecipeDao,
    viewModel: MainViewModel = hiltViewModel()
) {
    val recipes = remember { viewModel.swipeRecipes }

    val loading = viewModel.swipeLoading.collectAsState()

    if (loading.value) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "Loading Recipes!",
                color = BlueIsh,
                style = TextStyle(fontSize = sp_16)
            )
            Spacer(modifier = Modifier.height(dp_20))
            CircularProgressIndicator(color = BlueIsh)
        }
    } else {
        RecipeStack(
            dao = dao,
            items = recipes,
            onLike = {
                dao.insertRecipe(recipe = it)
            }, onReject = {
                with(RejectedRecipeCache.cache) {
                    if (size == 7) {
                        remove(first())
                        add(it)
                    } else {
                        add(it)
                    }
                }
            }) { data ->
            RecipeCard(data.recipe)
        }
    }
}

object RejectedRecipeCache {
    val cache = mutableListOf<Recipe>()
}