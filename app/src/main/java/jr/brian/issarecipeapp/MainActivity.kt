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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.issarecipeapp.model.local.AppDataStore
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.remote.ApiService
import jr.brian.issarecipeapp.util.SWIPE_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.FAV_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.HOME_ROUTE
import jr.brian.issarecipeapp.util.MEAL_DETAILS_ROUTE
import jr.brian.issarecipeapp.util.SETTINGS_ROUTE
import jr.brian.issarecipeapp.view.ui.pages.FavRecipesPage
import jr.brian.issarecipeapp.view.ui.pages.HomePage
import jr.brian.issarecipeapp.view.ui.pages.GenerateRecipePage
import jr.brian.issarecipeapp.view.ui.pages.RecipeSwipe
import jr.brian.issarecipeapp.view.ui.pages.SettingsPage
import jr.brian.issarecipeapp.view.ui.theme.IssaRecipeAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var dao: RecipeDao? = null
        @Inject set

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = AppDataStore(this)

        setContent {
            IssaRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val storedApiKey = dataStore.getApiKey.collectAsState(initial = "").value ?: ""
                    ApiService.ApiKey.userApiKey = storedApiKey

                    dao?.let {
                        AppUI(
                            dao = it,
                            storedApiKey = storedApiKey,
                            dataStore = dataStore
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppUI(
    dao: RecipeDao,
    storedApiKey: String,
    dataStore: AppDataStore
) {
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
                }, onNavToSwipe = {
                    navController.navigate(SWIPE_RECIPES_ROUTE) {
                        launchSingleTop = true
                    }
                },
                onNavToSettings = {
                    navController.navigate(SETTINGS_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        })
        composable(MEAL_DETAILS_ROUTE, content = {
            GenerateRecipePage(dao = dao)
        })
        composable(FAV_RECIPES_ROUTE, content = {
            FavRecipesPage(dao = dao)
        })
        composable(SWIPE_RECIPES_ROUTE, content = {
            RecipeSwipe(dao = dao)
        })
        composable(SETTINGS_ROUTE, content = {
            SettingsPage(
                apiKey = storedApiKey,
                dataStore = dataStore
            )
        })
    })
}