package jr.brian.issarecipeapp.util

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.view.ui.components.RejectedRecipeContentDialog
import jr.brian.issarecipeapp.view.ui.components.RejectedRecipeHistoryDialog
import jr.brian.issarecipeapp.view.ui.pages.RejectedRecipeCache
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM.dd.yy")

fun generateAskQuery(
    system: String? = null
) = "You are a 5 star chef. " +
        if (system.isNullOrBlank()) "" else "$system " +
                "\nLastly, only respond to questions that are about " +
                "preparing food, " +
                "cooking food, " +
                "providing recipes, " +
                "providing culinary advice, " +
                "or anything that generally has to do with any aspect of your job." +
                "Politely decline anything outside of that list."

fun generateRecipeQuery(
    occasion: String,
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
        "Also, title the recipe and surround it in '✨' for easy extraction. " +
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
fun SwipeHeaderLabel(dao: RecipeDao) {
    val isHistoryDialogShowing = remember {
        mutableStateOf(false)
    }

    val isContentDialogShowing = remember {
        mutableStateOf(false)
    }

    val selectedRecipe = remember {
        mutableStateOf(Recipe("", ""))
    }

    RejectedRecipeHistoryDialog(
        isShowing = isHistoryDialogShowing,
        recipes = RejectedRecipeCache.cache.distinct(),
        onSelectItem = {
            isContentDialogShowing.value = true
            selectedRecipe.value = it
        }
    )

    RejectedRecipeContentDialog(
        dao = dao,
        recipe = selectedRecipe.value,
        isShowing = isContentDialogShowing
    )

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            SWIPE_SCREEN_LABEL,
            color = BlueIsh,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable {
                isHistoryDialogShowing.value = !isHistoryDialogShowing.value
            },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "7", color = BlueIsh,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                painter = painterResource(id = R.drawable.baseline_history_24),
                contentDescription = "history",
                tint = BlueIsh,
                modifier = Modifier
                    .padding(end = 20.dp)

            )
        }
    }
}

fun getSpeechInputIntent(context: Context): Intent? {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Toast.makeText(context, "Speech not available", Toast.LENGTH_SHORT).show()
    } else {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now")
        return intent
    }
    return null
}