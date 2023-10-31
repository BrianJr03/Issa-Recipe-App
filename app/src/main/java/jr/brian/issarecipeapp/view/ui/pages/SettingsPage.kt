package jr.brian.issarecipeapp.view.ui.pages

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.AppDataStore
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.API_KEY_LABEL
import jr.brian.issarecipeapp.util.DIETARY_RESTRICTIONS_LABEL
import jr.brian.issarecipeapp.util.FOOD_ALLERGY_LABEL
import jr.brian.issarecipeapp.util.GENERATE_API_KEY_URL
import jr.brian.issarecipeapp.util.allergyOptions
import jr.brian.issarecipeapp.util.dietaryOptions
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
import jr.brian.issarecipeapp.view.ui.components.PresetOptionsDialog
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.Crimson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun SettingsPage(
    dao: RecipeDao,
    apiKey: String,
    dietaryRestrictions: String,
    foodAllergies: String,
    dataStore: AppDataStore
) {
    val scope = rememberCoroutineScope()

    val key = remember {
        mutableStateOf(apiKey)
    }

    val dietary = remember {
        mutableStateOf(dietaryRestrictions)
    }

    val allergies = remember {
        mutableStateOf(foodAllergies)
    }

    val pagerState = rememberPagerState()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

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
            dao = dao,
            apiKey = key,
            dietaryRestrictions = dietary,
            foodAllergies = allergies,
            onApiKeyValueChange = {
                scope.launch {
                    dataStore.saveApiKey(it)
                }
            },
            onDietaryValueChange = {
                scope.launch {
                    dataStore.saveDietaryRestrictions(it.lowercase())
                }
            },
            onAllergiesValueChange = {
                scope.launch {
                    dataStore.saveFoodAllergies(it.lowercase())
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus()
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Settings(
    dao: RecipeDao,
    apiKey: MutableState<String>,
    dietaryRestrictions: MutableState<String>,
    foodAllergies: MutableState<String>,
    onApiKeyValueChange: (String) -> Unit,
    onDietaryValueChange: (String) -> Unit,
    onAllergiesValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    val showErrorColorAPiKey = remember {
        mutableStateOf(false)
    }

    val isDietaryOptionsShowing = remember {
        mutableStateOf(false)
    }

    val isAllergyOptionsShowing = remember {
        mutableStateOf(false)
    }

    val isDeleteFavsInfoShowing = remember {
        mutableStateOf(true)
    }

    val deleteLabel = remember {
        mutableStateOf("Clear All Favorites")
    }

    PresetOptionsDialog(
        isShowing = isDietaryOptionsShowing,
        title = "Restrictions",
        options = dietaryOptions,
        onSelectItem = {
            dietaryRestrictions.value = it
            onDietaryValueChange(it)
        })

    PresetOptionsDialog(
        isShowing = isAllergyOptionsShowing,
        title = "Allergies",
        options = allergyOptions,
        onSelectItem = {
            foodAllergies.value = it
            onAllergiesValueChange(it)
        })

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(15.dp))

            DefaultTextField(
                label = "Save $DIETARY_RESTRICTIONS_LABEL",
                value = dietaryRestrictions.value,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onDietaryValueChange,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        tint = BlueIsh,
                        contentDescription = "View preset dietary restrictions",
                        modifier = Modifier.clickable {
                            focusManager.clearFocus()
                            isAllergyOptionsShowing.value = false
                            isDietaryOptionsShowing.value = !isDietaryOptionsShowing.value
                        }
                    )
                }
            )

            DefaultTextField(
                label = "Save $FOOD_ALLERGY_LABEL",
                value = foodAllergies.value,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onAllergiesValueChange,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        tint = BlueIsh,
                        contentDescription = "View preset food allergies",
                        modifier = Modifier.clickable {
                            focusManager.clearFocus()
                            isDietaryOptionsShowing.value = false
                            isAllergyOptionsShowing.value = !isAllergyOptionsShowing.value
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            DefaultTextField(
                label = API_KEY_LABEL,
                value = apiKey.value,
                onValueChange = onApiKeyValueChange,
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(15.dp))

            Column {
                when (!isDeleteFavsInfoShowing.value) {
                    true -> deleteLabel.value = "Long-Press To Confirm"
                    false -> deleteLabel.value = "Clear All Favorites"
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = deleteLabel.value,
                    color = Crimson,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            if (!isDeleteFavsInfoShowing.value) {
                                dao.removeAllRecipes()
                                Toast.makeText(context, "Cleared!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = {
                            scope.launch {
                                isDeleteFavsInfoShowing.value = !isDeleteFavsInfoShowing.value
                            }
                        })
                )
            }
        }
    }
}