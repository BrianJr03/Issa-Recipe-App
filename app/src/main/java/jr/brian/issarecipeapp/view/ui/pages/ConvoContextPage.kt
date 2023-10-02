package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.model.local.AppDataStore
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskContextPage(
    storedContext: String,
    dataStore: AppDataStore
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val conversationalContextText = remember { mutableStateOf("") }
    conversationalContextText.value = storedContext

    Scaffold {
        Spacer(Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                items(1) {
                    DefaultTextField(
                        value = conversationalContextText,
                        onValueChange = { text ->
                            conversationalContextText.value = text
                            scope.launch {
                                dataStore.saveAskContext(text)
                            }
                        },
                        label = "Provide Context",
                        modifier = Modifier
                            .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    scope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}