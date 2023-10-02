package jr.brian.issarecipeapp.model.repository

interface Repository {
    suspend fun getChatGptResponse(
        userPrompt: String,
        system: String? = null
    ): String
}