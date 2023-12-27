package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.DEFAULT_IMAGE_SIZE

interface Repository {
    suspend fun getAskResponse(
        dao: RecipeDao? = null,
        userPrompt: String,
        system: String? = null,
        model: String
    ): String

    suspend fun generateImageUrl(
        title: String,
        ingredients: String? = null,
        size: String = DEFAULT_IMAGE_SIZE
    ): String
}