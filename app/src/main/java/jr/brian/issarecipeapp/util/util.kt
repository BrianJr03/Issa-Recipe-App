package jr.brian.issarecipeapp.util

import androidx.compose.runtime.MutableState

private val occasionExamples =
    listOf(
        "breakfast",
        "Thanksgiving",
        "lunch",
        "Valentines day",
        "brunch",
        "dinner",
        "dessert"
    )

private val infoExamples =
    listOf(
        "Include a 3 day meal plan.",
        "Target 50 grams of carbs.",
        "Target 2000 calories.",
        "Organic ingredients only.",
        "List stores to visit",
        "List healthy alternatives.",
        "Respond in Spanish."
    )

val randomInfo = infoExamples.random()
val randomMealOccasion = occasionExamples.random()

fun generateRecipeQuery(
    occasion: MutableState<String>,
    partySize: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    ingredients: MutableState<String>,
    additionalInfo: MutableState<String>,
): String {
    return "Generate a recipe for ${occasion.value} that serves ${partySize.value} " +
            "using the following ingredients: ${ingredients.value}. " +
            "Keep in mind the following " +
            "dietary restrictions: ${dietaryRestrictions.value}. " +
            "Also note that I am allergic to ${foodAllergies.value}. " +
            if (additionalInfo.value.isNotBlank())
                "Lastly, here is some additional info for this recipe:" +
                        " ${additionalInfo.value}. Thanks!"
            else ""
}