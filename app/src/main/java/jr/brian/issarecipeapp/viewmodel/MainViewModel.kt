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
import jr.brian.issarecipeapp.model.local.getRandomRecipes
import jr.brian.issarecipeapp.model.remote.retrieveRecipes
import jr.brian.issarecipeapp.model.repository.Repository
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
        viewModelScope.launch {
            _swipeLoading.emit(true)
            delay(2000)
            _swipeLoading.emit(false)
            useNewRecipes()
            it.clear()
            it.addAll(currentSwipeRecipes)
        }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                refreshRecipes()
            }
        }
    }

    private suspend fun refreshRecipes() {
        newSwipeRecipes.clear()

        retrieveRecipes(
            onSuccess = { recipes ->
                val randomRecipes = getRandomRecipes(recipes, 5)
                newSwipeRecipes.addAll(randomRecipes)
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        onRecipesRetrieved()
                    }
                }
            },
            onError = { error ->
                newSwipeRecipes.add(
                    Recipe(
                        recipe = error.message,
                        name = "${error.code}"
                    )
                )
            }
        )
    }

    private suspend fun onRecipesRetrieved() {
        _swipeLoading.emit(false)
        if (!initialized) {
            initialized = true
            useNewRecipes()
            swipeRecipes.addAll(currentSwipeRecipes)
        }
    }


    private fun useNewRecipes() {
        currentSwipeRecipes.clear()
        currentSwipeRecipes.addAll(newSwipeRecipes)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                refreshRecipes()
            }
        }
    }
}