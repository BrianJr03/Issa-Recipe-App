package jr.brian.issarecipeapp.model.repository

import jr.brian.issarecipeapp.model.remote.ApiService

class RepoImpl : Repository {
   companion object  {
       private val apiService = ApiService
    }
    override suspend fun getChatGptResponse(
        userPrompt: String,
    ): String {
        return apiService.getChatGptResponse(
            userPrompt = userPrompt,
        )
    }
}