package jr.brian.issarecipeapp.model.remote

import jr.brian.issarecipeapp.model.local.Chat
import jr.brian.issarecipeapp.util.DEFAULT_CHAT_ROLE

/** WRITTEN BY: Collin Barber ~ https://github.com/CJCrafter **/

/**
 * Caches the initial request to make interactions easier (and String based).
 */
class CachedChatBot(
    apiKey: String,
    private val request: ChatCompletionRequest,
    prevChats: List<Chat>
) : ChatBot(apiKey) {
    private val previousChats = prevChats
    private val chatRole = DEFAULT_CHAT_ROLE
    fun generateResponse(
        content: String,
        role: String = chatRole
    ): String {
        request.includeChatHistory()
        request.messages.add(ChatMessage(role, content))
        val response = super.generateResponse(request)
        val temp = response.choices[0].message
        request.messages.add(temp)
        return temp.content
    }

    private fun ChatCompletionRequest.includeChatHistory() {
        previousChats.forEach {
            messages.add(ChatMessage(chatRole, it.text))
        }
    }
}