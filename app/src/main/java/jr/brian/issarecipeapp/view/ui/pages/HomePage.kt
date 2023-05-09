package jr.brian.issarecipeapp.view.ui.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.model.local.PresetMeal
import jr.brian.issarecipeapp.view.ui.components.PresetMealDialog
import jr.brian.issarecipeapp.view.ui.components.PresetMealsRow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(onNavToMealDetails: (PresetMeal) -> Unit) {
    val context = LocalContext.current

    Scaffold() {
        Column(modifier = Modifier.padding(it)) {
            Spacer(modifier = Modifier.height(15.dp))
            PresetMealsRow { presetMeal ->
                onNavToMealDetails(presetMeal)
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}