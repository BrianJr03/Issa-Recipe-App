package jr.brian.issarecipeapp.util

object MealType {
    private val mealTypeExamples =
        listOf(
            "breakfast",
            "Thanksgiving",
            "lunch",
            "Valentines day",
            "brunch",
            "dinner",
            "dessert"
        )

    val randomMealType = mealTypeExamples.random()
}