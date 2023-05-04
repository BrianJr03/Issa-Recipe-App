package jr.brian.issarecipeapp.model.local

import androidx.compose.ui.graphics.Color
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.PinkIsh

data class PresetMeal(
    val name: String,
    val picture: String,
    val boxColor: Color
)

val presetMeals = listOf(
    PresetMeal("BreakFast", "", BlueIsh),
    PresetMeal("Brunch", "", BlueIsh),
    PresetMeal("Lunch", "", BlueIsh),
    PresetMeal("Snack", "", BlueIsh),
    PresetMeal("Dinner", "", BlueIsh),
    PresetMeal("Desserts", "", BlueIsh),
)