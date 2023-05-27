package jr.brian.issarecipeapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.getRandomRecipes
import jr.brian.issarecipeapp.model.remote.retrieveRecipes
import jr.brian.issarecipeapp.model.repository.Repository
import jr.brian.issarecipeapp.util.CONNECTION_TIMEOUT_MSG
import jr.brian.issarecipeapp.util.MAX_CARDS_IN_STACK
import jr.brian.issarecipeapp.util.NO_RECIPES_TO_SWIPE_MSG
import jr.brian.issarecipeapp.util.UP_SIDE_DOWN_FACE_EMOJI
import jr.brian.issarecipeapp.view.ui.components.swipe_cards.InfiniteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    suspend fun getChefGptResponse(userPrompt: String) {
        _loading.emit(true)
        val aiResponse = repository.getChatGptResponse(userPrompt = userPrompt)
        _response.emit(aiResponse)
        _loading.emit(false)
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
                if (recipes.isEmpty() || recipes.size < MAX_CARDS_IN_STACK) {
                    newSwipeRecipes.add(
                        Recipe(
                            recipe = NO_RECIPES_TO_SWIPE_MSG,
                            name = UP_SIDE_DOWN_FACE_EMOJI // Upside down face emoji
                        )
                    )
                } else {
                    val randomRecipes = getRandomRecipes(recipes, MAX_CARDS_IN_STACK)
                    newSwipeRecipes.addAll(randomRecipes.filter {
                        it.recipe.lowercase() != CONNECTION_TIMEOUT_MSG
                    })
                }
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