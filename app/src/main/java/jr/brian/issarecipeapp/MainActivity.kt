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
import androidx.navigation.navArgument
import jr.brian.issarecipeapp.model.local.PresetMealNatType
import jr.brian.issarecipeapp.util.HOME_ROUTE
import jr.brian.issarecipeapp.util.MEAL_DETAILS_ROUTE
import jr.brian.issarecipeapp.view.ui.pages.HomePage
import jr.brian.issarecipeapp.view.ui.pages.MealDetailPage
import jr.brian.issarecipeapp.view.ui.theme.IssaRecipeAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IssaRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppUI()
                }
            }
        }
    }
}

@Composable
fun AppUI() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HOME_ROUTE, builder = {
        composable(HOME_ROUTE, content = {
            HomePage {
                navController.navigate("$MEAL_DETAILS_ROUTE/${it.name}") {
                    launchSingleTop = true
                }
            }
        })
        composable(
            "$MEAL_DETAILS_ROUTE/{mealName}",
            arguments = listOf(navArgument("mealName") { type = PresetMealNatType }),
            content = {
                val meal = it.arguments?.getString("mealName")
                meal?.let { m ->
                    MealDetailPage(mealName = m)
                }
            })
    })
}