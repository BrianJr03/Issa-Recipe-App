package jr.brian.issarecipeapp.model.local

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetMeal(
    val name: String,
    val picture: String
) : Parcelable

val presetMeals = listOf(
    PresetMeal("Breakfast", ""),
    PresetMeal("Brunch", ""),
    PresetMeal("Lunch", ""),
    PresetMeal("Snack", ""),
    PresetMeal("Dinner", ""),
    PresetMeal("Desserts", ""),
)
@Immutable
object PresetMealNatType : NavType<PresetMeal>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PresetMeal {
        return parseValue(requireNotNull(bundle.getString(key)))
    }

    override fun parseValue(
        value: String
    ): PresetMeal {
        return PresetMeal(name = value, picture = "")
    }

    override fun put(bundle: Bundle, key: String, value: PresetMeal) {
         bundle.apply {
            putString(key, value.name)
        }
    }
}
