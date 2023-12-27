package jr.brian.issarecipeapp.view.ui.pages

import android.app.Activity.RESULT_OK
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jr.brian.issarecipeapp.model.local.Chat
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.API_KEY_REQUIRED
import jr.brian.issarecipeapp.util.CHEF_GPT_LABEL
import jr.brian.issarecipeapp.util.NO_RESPONSE_MSG
import jr.brian.issarecipeapp.util.USER_LABEL
import jr.brian.issarecipeapp.util.dateFormatter
import jr.brian.issarecipeapp.util.getSpeechInputIntent
import jr.brian.issarecipeapp.util.showToast
import jr.brian.issarecipeapp.util.timeFormatter
import jr.brian.issarecipeapp.view.ui.components.ChatHeader
import jr.brian.issarecipeapp.view.ui.components.ChatSection
import jr.brian.issarecipeapp.view.ui.components.ChatTextFieldRow
import jr.brian.issarecipeapp.view.ui.components.EmptyPromptDialog
import jr.brian.issarecipeapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskPage(
    dao: RecipeDao,
    savedApiKey: String,
    savedAskContext: String,
    savedModel: String,
    viewModel: MainViewModel = hiltViewModel(),
    onNavToAskContext: () -> Unit,
    onNavToSettings: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val promptText = remember { mutableStateOf("") }

    val isEmptyPromptDialogShowing = remember { mutableStateOf(false) }
    val isChatGptTyping = remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val chats = remember { dao.getChats().toMutableStateList() }

    val scrollState = rememberScrollState()
    val chatListState = rememberLazyListState()

    LaunchedEffect(key1 = 1, block = {
        chatListState.animateScrollToItem(chats.size)
    })

    val speechToText = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        focusManager.clearFocus()
        val results = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        promptText.value =
            "${promptText.value}${if (promptText.value.isEmpty()) "" else " "}" + results?.get(0)
    }

    val sendOnClick = {
        focusManager.clearFocus()
        if (chats.isEmpty()) {
            scope.launch {
                chatListState.animateScrollToItem(0)
            }
        }
        if (savedApiKey.isEmpty()) {
            onNavToSettings()
            context.showToast(API_KEY_REQUIRED)
        } else if (promptText.value.isEmpty() || promptText.value.isBlank()) {
            isEmptyPromptDialogShowing.value = true
        } else {
            val prompt = promptText.value
            promptText.value = ""
            scope.launch {
                val myChat = Chat(
                    fullTimeStamp = LocalDateTime.now().toString(),
                    text = prompt,
                    dateSent = LocalDateTime.now().format(dateFormatter),
                    timeSent = LocalDateTime.now().format(timeFormatter),
                    senderLabel = USER_LABEL
                )
                chats.add(myChat)
                dao.insertChat(myChat)
                chatListState.animateScrollToItem(chats.size)
                viewModel.getAskResponse(
                    userPrompt = prompt,
                    context = savedAskContext,
                    model = savedModel,
                    dao = dao
                )
                val chatGptChat = Chat(
                    fullTimeStamp = LocalDateTime.now().toString(),
                    text = viewModel.response.value ?: NO_RESPONSE_MSG,
                    dateSent = LocalDateTime.now().format(dateFormatter),
                    timeSent = LocalDateTime.now().format(timeFormatter),
                    senderLabel = CHEF_GPT_LABEL
                )
                chats.add(chatGptChat)
                dao.insertChat(chatGptChat)
                chatListState.animateScrollToItem(chats.size)
            }

        }
    }

    EmptyPromptDialog(isShowing = isEmptyPromptDialogShowing)

    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .padding(it)
                .navigationBarsPadding()
        )
        {
            Spacer(Modifier.height(5.dp))

            ChatHeader(
                isChatGptTyping = isChatGptTyping.value,
                modifier = Modifier.padding(5.dp),
                onResetAllChats = {
                    chats.clear()
                    dao.removeAllChats()
                    Toast.makeText(
                        context,
                        "Conversation has been reset.",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onNavToAskContext = {
                    onNavToAskContext()
                }
            )

            ChatSection(
                chats = chats,
                listState = chatListState,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(.90f)
                    .clickable(
                        interactionSource = interactionSource, indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) { chat ->
                chats.remove(chat)
                dao.removeChat(chat)
            }

            ChatTextFieldRow(
                promptText = promptText.value,
                textFieldOnValueChange = { text -> promptText.value = text },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp)
                    .onFocusEvent { event ->
                        if (event.isFocused) {
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    },
                sendIconModifier = Modifier
                    .size(30.dp)
                    .clickable { sendOnClick() },
                micIconModifier = Modifier
                    .size(25.dp)
                    .clickable {
                        speechToText.launch(getSpeechInputIntent(context))
                    }
            )
        }
    }
}