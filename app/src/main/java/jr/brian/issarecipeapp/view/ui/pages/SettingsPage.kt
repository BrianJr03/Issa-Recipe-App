package jr.brian.issarecipeapp.view.ui.pages

import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.model.local.AppDataStore
import jr.brian.issarecipeapp.util.API_KEY_LABEL
import jr.brian.issarecipeapp.util.GENERATE_API_KEY_URL
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun SettingsPage(
    apiKey: String,
    dataStore: AppDataStore
) {
    val scope = rememberCoroutineScope()

    val key = remember {
        mutableStateOf(apiKey)
    }

    val pagerState = rememberPagerState()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pagerState.currentPage == 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                } else {
                    isEnabled = false
                    backPressedDispatcher?.onBackPressed()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        backPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    Scaffold {
        Settings(
            apiKey = key, onApiKeyValueChange = {
                scope.launch {
                    dataStore.saveApiKey(it)
                }
            }, modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

@Composable
fun Settings(
    apiKey: MutableState<String>,
    onApiKeyValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val showErrorColorAPiKey = remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(15.dp))
            DefaultTextField(
                label = API_KEY_LABEL,
                value = apiKey,
                onValueChange = onApiKeyValueChange,
                modifier = Modifier.fillMaxWidth(),
                isShowingErrorColor = showErrorColorAPiKey
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(GENERATE_API_KEY_URL)
                    context.startActivity(intent)
                },
            ) {
                Text(text = "Generate API Key")
            }
        }
    }
}