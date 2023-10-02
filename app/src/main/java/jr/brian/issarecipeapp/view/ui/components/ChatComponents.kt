package jr.brian.issarecipeapp.view.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.Chat
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.CHEF_GPT_LABEL
import jr.brian.issarecipeapp.util.copyToastMessages
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import kotlinx.coroutines.launch

@Composable
fun ChatHeader(
    isChatGptTyping: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    headerTextModifier: Modifier = Modifier,
    onResetAllChats: () -> Unit,
    onNavToAskContext: () -> Unit,
) {
    val isDeleteDialogShowing = remember { mutableStateOf(false) }

    DeleteDialog(
        title = "Reset this Conversation?",
        isShowing = isDeleteDialogShowing,
    ) {
        onResetAllChats()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (isChatGptTyping.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(.1f))
                Text(
                    "ChefGPT is typing",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                LottieLoading(
                    isShowing = isChatGptTyping,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.weight(.1f))
            }
        }
        else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(top = 15.dp, bottom = 15.dp)
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    "Ask",
                    color = BlueIsh,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = headerTextModifier
                )
                Spacer(modifier = Modifier.weight(.1f))
                Icon(
                    painter = painterResource(id = R.drawable.baseline_more_24),
                    contentDescription = "More",
                    tint = BlueIsh,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onNavToAskContext()
                        }
                )
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                    contentDescription = "Reset Conversation",
                    tint = BlueIsh,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            isDeleteDialogShowing.value = !isDeleteDialogShowing.value
                        }
                )
                Spacer(modifier = Modifier.width(15.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatSection(
    dao: RecipeDao,
    chats: MutableList<Chat>,
    listState: LazyListState,
    scaffoldState: ScaffoldState,
    isChefGptTyping: State<Boolean>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }

    if (chats.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.height(50.dp)
        ) {
            Text(
                "No Chats Recorded",
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }

    LazyColumn(modifier = modifier, state = listState) {
        items(chats.size) { index ->
            val chat = chats[index]
            val isHumanChatBox = chat.senderLabel != CHEF_GPT_LABEL
            val isDeleteDialogShowing = remember { mutableStateOf(false) }

            val isShowingLoadingBar = remember {
                derivedStateOf {
                    (isChefGptTyping.value && index == chats.size - 1)
                }
            }

            DeleteDialog(
                title = "Delete this Chat?",
                isShowing = isDeleteDialogShowing,
            ) {
                chats.remove(chat)
                dao.removeChat(chat)
                scope.launch { scaffoldState.drawerState.close() }
            }

            ChatBox(
                text = chat.text,
                dateSent = chat.dateSent,
                timeSent = chat.timeSent,
                senderLabel = chat.senderLabel,
                isHumanChatBox = isHumanChatBox,
                isChefGptTyping = isShowingLoadingBar,
                modifier = Modifier
                    .padding(10.dp)
                    .indication(interactionSource, LocalIndication.current)
                    .animateItemPlacement(),
                onDeleteChat = {
                    isDeleteDialogShowing.value = true
                }
            ) {
                scope.launch {
                    val snackResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Copy all text?",
                        actionLabel = "Yes",
                        duration = SnackbarDuration.Short
                    )
                    when (snackResult) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> {
                            clipboardManager.setText(AnnotatedString((chat.text)))
                            Toast.makeText(
                                context,
                                copyToastMessages.random(),
                                Toast.LENGTH_LONG
                            ).show()
                            focusManager.clearFocus()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatBox(
    text: String,
    dateSent: String,
    timeSent: String,
    senderLabel: String,
    isHumanChatBox: Boolean,
    isChefGptTyping: State<Boolean>,
    modifier: Modifier = Modifier,
    onDeleteChat: () -> Unit,
    onLongCLick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isChatInfoShowing = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(.8f),
            horizontalAlignment = if (isHumanChatBox) Alignment.End else Alignment.Start
        ) {
            AnimatedVisibility(visible = isChatInfoShowing.value) {
                Column(
                    horizontalAlignment = if (isHumanChatBox) Alignment.End else Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = if (isHumanChatBox) 0.dp else 10.dp,
                            end = if (isHumanChatBox) 10.dp else 0.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            senderLabel,
                            modifier = Modifier
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("•")
                        Spacer(Modifier.width(5.dp))
                        Text(dateSent)
                        Spacer(Modifier.width(5.dp))
                        Text("•")
                        Spacer(Modifier.width(5.dp))
                        Text(timeSent)
                    }
                    Row(
                        modifier = Modifier.padding(
                            start = if (isHumanChatBox) 0.dp else 10.dp,
                            end = if (isHumanChatBox) 10.dp else 0.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Delete",
                            modifier = Modifier.clickable {
                                onDeleteChat()
                                isChatInfoShowing.value = false
                            }
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isChefGptTyping.value) {
                    CircularProgressIndicator(
                        color = BlueIsh,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isHumanChatBox) BlueIsh else Color.Gray)
                        .combinedClickable(
                            onClick = {
                                focusManager.clearFocus()
                                isChatInfoShowing.value = !isChatInfoShowing.value
                            },
                            onLongClick = { onLongCLick() },
                        )
                ) {
                    CompositionLocalProvider {
                        SelectionContainer {
                            Text(
                                text,
                                color = Color.White,
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatTextFieldRow(
    promptText: String,
    textFieldOnValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    sendIconModifier: Modifier = Modifier,
    micIconModifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        value = promptText,
        onValueChange = textFieldOnValueChange,
        label = {
            Text(
                text = "Enter a prompt",
                color = BlueIsh,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = BlueIsh,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = BlueIsh
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                tint = BlueIsh,
                contentDescription = "Mic",
                modifier = micIconModifier
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_send_24),
                tint = BlueIsh,
                contentDescription = "Send Message",
                modifier = sendIconModifier
            )
        }
    )

    Spacer(Modifier.height(15.dp))
}