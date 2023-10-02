package jr.brian.issarecipeapp.model.local

import androidx.room.*

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: Chat)

    @Query("SELECT * FROM chats")
    fun getChats(): List<Chat>

    @Delete
    fun removeChat(chat: Chat)

    @Query("DELETE FROM chats")
    fun removeAllChats()
}