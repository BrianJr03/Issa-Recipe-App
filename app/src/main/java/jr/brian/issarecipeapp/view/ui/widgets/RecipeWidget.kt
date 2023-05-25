package jr.brian.issarecipeapp.view.ui.widgets

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import jr.brian.issarecipeapp.util.NO_GENERATED_RECIPE

object RecipeWidget : GlanceAppWidget() {
    val recipeKey = stringPreferencesKey("recipe")
//
//    @Composable
//    override fun Content() {
//
//    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val recipe = currentState(key = recipeKey) ?: NO_GENERATED_RECIPE

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = recipe, style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.White)
                    )
                )
                Button(
                    text = "Click",
                    onClick = actionRunCallback(GenerateRecipeActionCallback::class.java)
                )
            }
        }
    }
}

class RecipeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = RecipeWidget
}

class GenerateRecipeActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[RecipeWidget.recipeKey] = "Recipe 1" // TODO - add response here
        }
        RecipeWidget.update(context, glanceId)
    }
}