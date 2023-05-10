package jr.brian.issarecipeapp.model.local

import androidx.room.*

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getRecipes(): List<Recipe>

    @Delete
    fun removeRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    fun removeAllRecipes()
}