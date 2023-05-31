package jr.brian.issarecipeapp.model.local

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Update
    fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getRecipes(): List<Recipe>

    @Delete
    fun removeRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    fun removeAllRecipes()

    // ----  FOLDERS ---- //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(folder: RecipeFolder)

    @Update
    fun updateFolder(folder: RecipeFolder)

    @Query("SELECT * FROM folders")
    fun getFolders(): List<RecipeFolder>

    @Delete
    fun removeFolder(folder: RecipeFolder)

    @Query("DELETE FROM folders")
    fun removeAllFolders()

    @Query("DELETE FROM recipes WHERE folderName LIKE :folderName")
    fun removeAllRecipesInFolder(folderName: String)

    @RawQuery
    fun getRecipeRawQuery(query: SupportSQLiteQuery): List<Recipe>

    fun getRecipesByFolder(folderName: String): List<Recipe> {
        val query = SimpleSQLiteQuery(
            "SELECT * FROM recipes WHERE folderName LIKE ?;",
            arrayOf(folderName)
        )
        return getRecipeRawQuery(query)
    }

    @Query("DELETE FROM recipes WHERE folderName LIKE :folderName")
    fun removeAllChatsByConvo(folderName: String)
}