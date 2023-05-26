package jr.brian.issarecipeapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.EXPLORE_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.FAV_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.HOME_ROUTE
import jr.brian.issarecipeapp.util.MEAL_DETAILS_ROUTE
import jr.brian.issarecipeapp.view.ui.components.swipe_cards.RecipeSwipe
import jr.brian.issarecipeapp.view.ui.pages.FavRecipesPage
import jr.brian.issarecipeapp.view.ui.pages.HomePage
import jr.brian.issarecipeapp.view.ui.pages.GenerateRecipePage
import jr.brian.issarecipeapp.view.ui.theme.IssaRecipeAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var dao: RecipeDao? = null
        @Inject set

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IssaRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    dao?.let { AppUI(it) }
                }
            }
        }
    }
}

@Composable
fun AppUI(dao: RecipeDao) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HOME_ROUTE, builder = {
        composable(HOME_ROUTE, content = {
            HomePage(
                onNavToMealDetails = {
                    navController.navigate(MEAL_DETAILS_ROUTE) {
                        launchSingleTop = true
                    }
                }, onNavToFavRecipes = {
                    navController.navigate(FAV_RECIPES_ROUTE) {
                        launchSingleTop = true
                    }
                }, onNavToExplore = {
                    navController.navigate(EXPLORE_RECIPES_ROUTE) {
                        launchSingleTop = true
                    }
                })
        })
        composable(
            MEAL_DETAILS_ROUTE,
            content = {
                GenerateRecipePage(dao = dao)
            })
        composable(FAV_RECIPES_ROUTE, content = {
            FavRecipesPage(dao = dao)
        })
        composable(EXPLORE_RECIPES_ROUTE, content = {
            RecipeSwipe(dao = dao)
        })
    })
}