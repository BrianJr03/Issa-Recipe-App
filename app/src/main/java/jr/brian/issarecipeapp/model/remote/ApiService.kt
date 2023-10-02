package jr.brian.issarecipeapp.model.remote

import jr.brian.issarecipeapp.util.GPT_3_5_TURBO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface ApiService {

    object ApiKey {
        var userApiKey = ""
    }

    companion object {
        suspend fun getChatGptResponse(
            userPrompt: String,
            system: String? = null
        ): String {
            var aiResponse: String
            try {
                withContext(Dispatchers.IO) {
                    val key = ApiKey.userApiKey
                    val request = ChatBot.ChatCompletionRequest(
                        model = GPT_3_5_TURBO,
                        systemContent = "You are a 5 star chef. " +
                                if (system.isNullOrBlank()) "" else "$system " +
                                "\nLastly, only respond to questions that are about " +
                                "preparing food, " +
                                "cooking food, " +
                                "providing recipes, " +
                                "providing culinary advice, " +
                                "or anything that generally has to do with any aspect of your job." +
                                "Politely decline anything outside of that list."
                    )
                    val bot = CachedChatBot(
                        key,
                        request
                    )
                    aiResponse = bot.generateResponse(userPrompt)
                }
            } catch (e: SocketTimeoutException) {
                aiResponse = "Connection timed out. Please try again."
            } catch (e: java.lang.IllegalArgumentException) {
                aiResponse = "ERROR: ${e.message}"
            } catch (e: UnknownHostException) {
                aiResponse = "ERROR: ${e.message}.\n\n" +
                        "This could indicate no/very poor internet connection. " +
                        "Please check your connection and try again."
            }
            return aiResponse
        }
    }
}