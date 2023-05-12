package jr.brian.issarecipeapp.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.RECIPE_NAME_MAX_CHAR_COUNT
import jr.brian.issarecipeapp.util.customTextSelectionColors
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun ShowDialog(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)?,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    isShowing: MutableState<Boolean>,
) {
    if (isShowing.value) {
        AlertDialog(
            title = { Text(title, fontSize = 22.sp) },
            text = content,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            onDismissRequest = { isShowing.value = false },
            modifier = modifier,
        )
    }
}

@Composable
fun PresetOptionsDialog(
    isShowing: MutableState<Boolean>,
    title: String,
    options: List<String>,
    onSelectItem: (String) -> Unit,
) {
    ShowDialog(
        title = "Preset $title",
        content = {
            LazyColumn() {
                items(options.size) { index ->
                    val option = options[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectItem(option)
                                isShowing.value = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            option,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    if (index != options.size - 1) {
                        Divider()
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        isShowing = isShowing
    )
}

@Composable
fun RecipeNameDialog(
    isShowing: MutableState<Boolean>,
    isShowingErrorColor: MutableState<Boolean>,
    name: MutableState<String>,
    onConfirmClick: () -> Unit
) {
    ShowDialog(
        title = "Name this Recipe",
        content = {
            DefaultTextField(
                label = "name",
                value = name,
                isShowingErrorColor = isShowingErrorColor,
                maxCount = RECIPE_NAME_MAX_CHAR_COUNT
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirmClick()
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            Button(onClick = {
                name.value = ""
                isShowingErrorColor.value = false
                isShowing.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        isShowing = isShowing
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeContentDialog(
    dao: RecipeDao,
    recipe: Recipe,
    favRecipes: SnapshotStateList<Recipe>,
    isShowing: MutableState<Boolean>,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val isRenameFieldShowing = remember {
        mutableStateOf(false)
    }

    val isRenameLabelShowing = remember {
        mutableStateOf(true)
    }

    val isLongPressLabelShowing = remember {
        mutableStateOf(false)
    }

    val newRecipeName = remember {
        mutableStateOf(recipe.name)
    }

    val showTextFieldErrorColor = remember {
        mutableStateOf(false)
    }

    val renameOnClick = {
        scope.launch {
            isRenameLabelShowing.value = isRenameFieldShowing.value
            delay(300)
            isRenameFieldShowing.value = !isRenameFieldShowing.value
        }
    }

    val saveNewName = {
        val newRecipe = Recipe(recipe.recipe, newRecipeName.value)
        favRecipes[favRecipes.indexOf(recipe)] = newRecipe
        dao.updateRecipe(recipe = newRecipe)
        isRenameFieldShowing.value = false
        isRenameLabelShowing.value = true
    }

    val deleteOnLongClick = {
        dao.removeRecipe(recipe = recipe)
        onDelete()
    }

    ShowDialog(
        title = recipe.name,
        content = {
            LazyColumn(content = {
                items(1) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            renameOnClick()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                tint = BlueIsh,
                                modifier = Modifier.size(20.dp),
                                contentDescription = "Edit"
                            )
                        }

                        AnimatedVisibility(visible = isRenameLabelShowing.value) {
                            Text(
                                text = "Rename Recipe",
                                color = BlueIsh,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clickable {
                                        renameOnClick()
                                    }
                            )
                        }

                        AnimatedVisibility(visible = isRenameFieldShowing.value) {
                            IconButton(
                                modifier = Modifier.padding(start = 20.dp),
                                onClick = {
                                    if (newRecipeName.value.isNotBlank()) {
                                        saveNewName()
                                    } else {
                                        showTextFieldErrorColor.value = true
                                    }
                                }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_check_24),
                                    tint = BlueIsh,
                                    modifier = Modifier.size(30.dp),
                                    contentDescription = "Save"
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isRenameFieldShowing.value) {
                        DefaultTextField(
                            label = "Rename Recipe",
                            value = newRecipeName,
                            isShowingErrorColor = showTextFieldErrorColor,
                            maxCount = RECIPE_NAME_MAX_CHAR_COUNT
                        )
                    }

                    CompositionLocalProvider(
                        LocalTextSelectionColors provides customTextSelectionColors
                    ) {
                        SelectionContainer {
                            Text(
                                recipe.recipe,
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
            })
        },
        confirmButton = {
            Text("Nice!",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        isShowing.value = false
                        if (isRenameFieldShowing.value) {
                            saveNewName()
                        }
                    })
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(visible = isLongPressLabelShowing.value) {
                    Text(
                        text = "Long-Press to ",
                        fontSize = 16.sp,
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = { deleteOnLongClick() }
                        )
                    )
                }

                Text(
                    "Delete",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp,
                            end = 10.dp
                        )
                        .combinedClickable(
                            onClick = {
                                scope.launch {
                                    isLongPressLabelShowing.value = true
                                    delay(3000)
                                    isLongPressLabelShowing.value = false
                                }
                            },
                            onLongClick = { deleteOnLongClick() }
                        )
                )
            }
        },
        isShowing = isShowing
    )
}