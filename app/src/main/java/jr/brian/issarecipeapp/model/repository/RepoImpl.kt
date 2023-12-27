package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.remote.ApiService

class RepoImpl : Repository {
    companion object {
        private val apiService = ApiService
    }

    override suspend fun getAskResponse(
        dao: RecipeDao?,
        userPrompt: String,
        system: String?,
        model: String
    ): String {
        return apiService.getAskResponse(
            userPrompt = userPrompt,
            system = system,
            model = model,
            dao = dao
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