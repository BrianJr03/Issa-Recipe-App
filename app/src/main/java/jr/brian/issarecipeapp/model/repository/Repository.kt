package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.util.DEFAULT_IMAGE_SIZE

interface Repository {
    suspend fun getAskResponse(
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