package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.model.remote.ApiService

class RepoImpl : Repository {
    companion object {
        private val apiService = ApiService
    }

    override suspend fun getChatGptResponse(
        userPrompt: String,
        system: String?
    ): String {
        return apiService.getChatGptResponse(
            userPrompt = userPrompt,
            system = system
        )
    }

    override suspend fun generateImageUrl(
        title: String,
        ingredients: String?,
        size: String
    ): String {
        return apiService.generateImageUrl(
            title = title,
            ingredients = ingredients,
            size = size
        )
    }
}