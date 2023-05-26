package jr.brian.issarecipeapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.repository.Repository
import jr.brian.issarecipeapp.util.generateRecipeQuery
import jr.brian.issarecipeapp.view.ui.components.swipe_cards.InfiniteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _response = MutableStateFlow<String?>(null)
    val response = _response.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _swipeLoading = MutableStateFlow(false)
    val swipeLoading = _swipeLoading.asStateFlow()

    private val currentSwipeRecipes = mutableStateListOf<Recipe>()
    private val newSwipeRecipes = mutableListOf<Recipe>()
    private var initialized = false

    companion object {
        var textToSpeech: TextToSpeech? = null
    }

    suspend fun getChefGptResponse(userPrompt: String) {
        _loading.emit(true)
        val aiResponse = repository.getChatGptResponse(userPrompt = userPrompt)
        _response.emit(aiResponse)
        _loading.emit(false)
        // textToSpeech(context = context, text = aiResponse)
    }

    @Suppress("unused")
    fun textToSpeech(context: Context, text: String) {
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    if (!txtToSpeech.isSpeaking) {
                        txtToSpeech.language = Locale.getDefault()
                        txtToSpeech.setSpeechRate(1.0f)
                        txtToSpeech.stop()
                        txtToSpeech.speak(
                            text,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }
            }
        }
    }

    @Suppress("unused")
    fun stopSpeech() {
        textToSpeech?.stop()
    }

    val swipeRecipes = InfiniteList {
        useNewRecipes()
        it.clear()
        it.addAll(currentSwipeRecipes)
    }

    init {
        refreshRecipes(10)
    }

    private fun refreshRecipes(limit: Int = 1) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newSwipeRecipes.clear()

                _swipeLoading.emit(true)

                val query = generateRecipeQuery(
                    occasion = "any",
                    partySize = "any",
                    dietaryRestrictions = "none", // TODO - GIVE USER OPTION TO SAVE INFO IN SETTINGS
                    foodAllergies = "none", // TODO - GIVE USER OPTION TO SAVE INFO IN SETTINGS
                    ingredients = "random",
                    additionalInfo = "please provide very short recipes"
                )

                val aiResponse =  "repository.getChatGptResponse(userPrompt = query)"

                for (i in 1..limit) {
                    newSwipeRecipes.add(Recipe(aiResponse, "Recipe $i"))
                }

                _swipeLoading.emit(false)

                if (!initialized) {
                    initialized = true
                    delay(1000)
                    useNewRecipes()
                    swipeRecipes.addAll(currentSwipeRecipes)
                }
            }
        }
    }

    private fun useNewRecipes() {
        currentSwipeRecipes.clear()
        currentSwipeRecipes.addAll(newSwipeRecipes)
        refreshRecipes(currentSwipeRecipes.size)
    }
}