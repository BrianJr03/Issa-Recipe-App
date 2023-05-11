package jr.brian.issarecipeapp.util

import androidx.compose.foundation.text.selection.TextSelectionColors
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh

const val HOME_ROUTE = "home"
const val MEAL_DETAILS_ROUTE = "meal-details"
const val FAV_RECIPES_ROUTE = "fav-recipes"
const val GPT_3_5_TURBO = "gpt-3.5-turbo"

const val PARTY_SIZE_LABEL = "Party Size *"
const val DIETARY_RESTRICTIONS_LABEL = "Dietary Restrictions"
const val FOOD_ALLERGY_LABEL = "Food Allergies"
const val INGREDIENTS_LABEL = "Ingredients *"

val customTextSelectionColors = TextSelectionColors(
    handleColor = BlueIsh,
    backgroundColor = BlueIsh
)