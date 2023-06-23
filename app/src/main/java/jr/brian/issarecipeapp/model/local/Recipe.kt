package jr.brian.issarecipeapp.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey @JvmField val recipe: String,
    @JvmField var name: String
)

fun getRandomRecipes(recipes: List<Recipe>, count: Int): List<Recipe> {
    require(count <= recipes.size) { "Count should not exceed the size of the recipe list." }

    val selectedIndices = mutableSetOf<Int>()
    val randomRecipes = mutableListOf<Recipe>()

    while (randomRecipes.size < count) {
        val availableIndices = (recipes.indices).filter { !selectedIndices.contains(it) }
        if (availableIndices.isEmpty()) {
            break
        }
        val randomIndex = availableIndices.random()
        selectedIndices.add(randomIndex)
        randomRecipes.add(recipes[randomIndex])
    }

    return randomRecipes
}
