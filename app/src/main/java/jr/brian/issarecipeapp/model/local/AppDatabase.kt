package jr.brian.issarecipeapp.model.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class, RecipeFolder::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): RecipeDao
}