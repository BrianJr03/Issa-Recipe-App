package jr.brian.issarecipeapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.speech.tts.TextToSpeech
import jr.brian.issarecipeapp.model.repository.Repository
import java.util.*

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _response = MutableStateFlow<String?>(null)
    val response = _response.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    companion object {
        var textToSpeech: TextToSpeech? = null
    }

    suspend fun getChatGptResponse(
        context: Context,
        userPrompt: String,

        ) {
        _loading.emit(true)
        val aiResponse = repository.getChatGptResponse(userPrompt = userPrompt)
        _response.emit(aiResponse)
        _loading.emit(false)
        // textToSpeech(context = context, text = aiResponse)
    }

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

    fun stopSpeech() {
        textToSpeech?.stop()
    }
}