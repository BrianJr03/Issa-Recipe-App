package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.util.DEFAULT_IMAGE_SIZE

interface Repository {
    suspend fun getChatGptResponse(
        userPrompt: String,
        system: String? = null
    ): String

    suspend fun generateImageUrl(
        title: String,
        ingredients: String? = null,
        size: String = DEFAULT_IMAGE_SIZE
    ): String
}