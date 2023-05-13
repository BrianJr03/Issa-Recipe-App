package jr.brian.issarecipeapp.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "folders")
@TypeConverters(RecipeFolderTypeConverters::class)
data class RecipeFolder(
    @PrimaryKey val name: String,
    val recipes: List<Recipe>
)

class RecipeFolderTypeConverters {
    @TypeConverter
    fun fromRecipeList(recipeList: List<Recipe>): String {
        return Gson().toJson(recipeList)
    }

    @TypeConverter
    fun toRecipeList(recipeJson: String): List<Recipe> {
        val recipeType = object : TypeToken<List<Recipe>>() {}.type
        return Gson().fromJson(recipeJson, recipeType)
    }
}