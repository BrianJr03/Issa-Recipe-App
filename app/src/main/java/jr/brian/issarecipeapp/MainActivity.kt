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
import jr.brian.issarecipeapp.util.ASK_CONTEXT_ROUTE
import jr.brian.issarecipeapp.util.ASK_ROUTE
import jr.brian.issarecipeapp.util.SWIPE_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.FAV_RECIPES_ROUTE
import jr.brian.issarecipeapp.util.GPT_3_5_TURBO
import jr.brian.issarecipeapp.util.HOME_ROUTE
import jr.brian.issarecipeapp.util.MEAL_DETAILS_ROUTE
import jr.brian.issarecipeapp.util.SETTINGS_ROUTE
import jr.brian.issarecipeapp.view.ui.pages.AskContextPage
import jr.brian.issarecipeapp.view.ui.pages.AskPage
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
                    val storedApiKey = dataStore.getApiKey.collectAsState(initial = "").value
                        ?: ""
                    val dietaryRestrictions =
                        dataStore.getDietaryRestrictions.collectAsState(initial = "none").value
                            ?: "none"
                    val foodAllergies =
                        dataStore.getFoodAllergies.collectAsState(initial = "none").value
                            ?: "none"
                    val gptModel =
                        dataStore.getGptModel.collectAsState(initial = GPT_3_5_TURBO).value
                            ?: GPT_3_5_TURBO
                    val askContext =
                        dataStore.getAskContext.collectAsState(initial = "").value
                            ?: ""
                    val isImageGenerationEnabled =
                        dataStore.getIsImageGenerationEnabled.collectAsState(initial = "false").value
                            ?: ""

                    ApiService.ApiKey.userApiKey = storedApiKey

                    dao?.let {
                        AppUI(
                            dao = it,
                            savedApiKey = storedApiKey,
                            savedDietaryRestrictions = dietaryRestrictions,
                            savedFoodAllergies = foodAllergies,
                            savedGptModel = gptModel,
                            storedAskContext = askContext,
                            isImageGenerationEnabled = isImageGenerationEnabled,
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
    savedApiKey: String,
    savedDietaryRestrictions: String,
    savedFoodAllergies: String,
    savedGptModel: String,
    storedAskContext: String,
    isImageGenerationEnabled: String,
    dataStore: AppDataStore
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HOME_ROUTE, builder = {
        composable(HOME_ROUTE, content = {
            HomePage(
                onNavToAsk = {
                    navController.navigate(ASK_ROUTE) {
                        launchSingleTop = true
                    }
                },
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
        composable(ASK_ROUTE, content = {
            AskPage(
                dao = dao,
                savedApiKey = savedApiKey,
                savedAskContext = storedAskContext,
                savedModel = savedGptModel,
                onNavToAskContext = {
                    navController.navigate(ASK_CONTEXT_ROUTE) {
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
        composable(ASK_CONTEXT_ROUTE, content = {
            AskContextPage(storedContext = storedAskContext, dataStore = dataStore)
        })
        composable(MEAL_DETAILS_ROUTE, content = {
            GenerateRecipePage(
                dao = dao,
                dataStore = dataStore,
                dietaryRestrictions = savedDietaryRestrictions,
                foodAllergies = savedFoodAllergies,
                gptModel = savedGptModel,
                isImageGenerationEnabled = isImageGenerationEnabled,
                onNavToSettings = {
                    navController.navigate(SETTINGS_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        })
        composable(FAV_RECIPES_ROUTE, content = {
            FavRecipesPage(dao = dao)
        })
        composable(SWIPE_RECIPES_ROUTE, content = {
            RecipeSwipe(dao = dao)
        })
        composable(SETTINGS_ROUTE, content = {
            SettingsPage(
                dao = dao,
                apiKey = savedApiKey,
                dietaryRestrictions = savedDietaryRestrictions,
                foodAllergies = savedFoodAllergies,
                gptModel = savedGptModel,
                isImageGenerationEnabled = isImageGenerationEnabled,
                dataStore = dataStore,
            )
        })
    })
}