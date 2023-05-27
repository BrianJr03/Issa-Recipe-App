package jr.brian.issarecipeapp.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import java.util.Calendar

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

fun isWithinTimeRange(startHour: Int, endHour: Int): Boolean {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    return currentHour in startHour..endHour
}

val isBreakfastTime = isWithinTimeRange(breakfastStartHour, breakfastEndHour)
val isLunchTime = isWithinTimeRange(lunchStartHour, lunchEndHour)

fun getPath(): String {
    return if (isBreakfastTime) {
        "breakfast"
    } else if (isLunchTime) {
        "lunch"
    } else {
        "dinner"
    }
}

@Composable
fun SwipeHeaderLabel() {
    Text(
        SWIPE_SCREEN_LABEL,
        Modifier.fillMaxWidth(),
        color = BlueIsh,
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}