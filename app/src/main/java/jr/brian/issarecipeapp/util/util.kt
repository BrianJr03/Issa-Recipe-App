package jr.brian.issarecipeapp.util

import androidx.compose.foundation.text.selection.TextSelectionColors
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh

val occasionOptions =
    listOf(
        "breakfast",
        "brunch",
        "lunch",
        "snack",
        "dinner",
        "dessert",
        "any occasion"
    )

val dietaryOptions = listOf(
    "lactose intolerance",
    "gluten intolerance",
    "vegetarian",
    "vegan",
    "kosher",
    "none"
)

val allergyOptions = listOf(
    "dairy",
    "peanuts",
    "fish",
    "soy",
    "sesame",
    "none"
)

private val infoExamples =
    listOf(
        "Include a 3 day meal plan.",
        "Target 50 grams of carbs.",
        "Target 2000 calories.",
        "Organic ingredients only.",
        "List stores to visit",
        "List healthy alternatives.",
        "Respond in Spanish.",
        "Include only pizza recipes"
    )

val randomInfo = infoExamples.random()
val randomMealOccasion = occasionOptions.random()

fun generateRecipeQuery(
    occasion:String,
    partySize: String,
    dietaryRestrictions: String,
    foodAllergies: String,
    ingredients: String,
    additionalInfo: String,
) = "Generate a recipe for $occasion that serves $partySize " +
        "using the following ingredients: $ingredients. " +
        "Keep in mind the following " +
        "dietary restrictions: $dietaryRestrictions. " +
        "Also note that I am allergic to $foodAllergies. " +
        "Please include the estimated calories, fat, carbs, protein " +
        "and preparation / cook time. " +
        if (additionalInfo.isNotBlank())
            "Lastly, here is some additional info for this recipe:" +
                    " $additionalInfo. Thanks!"
        else ""


val customTextSelectionColors = TextSelectionColors(
    handleColor = BlueIsh,
    backgroundColor = BlueIsh
)