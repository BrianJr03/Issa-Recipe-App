package jr.brian.issarecipeapp.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(@PrimaryKey val recipe: String)