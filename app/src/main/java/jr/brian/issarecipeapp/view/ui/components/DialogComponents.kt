package jr.brian.issarecipeapp.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.local.RecipeFolder
import jr.brian.issarecipeapp.util.NO_REJECTED_RECIPES_DIALOG_LABEL
import jr.brian.issarecipeapp.util.RECIPE_NAME_MAX_CHAR_COUNT
import jr.brian.issarecipeapp.util.REJECTED_RECIPES_DIALOG_LABEL
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
fun OptionsDialog(
    isShowing: MutableState<Boolean>,
    title: String,
    options: List<String>,
    onSelectItem: (String) -> Unit,
) {
    ShowDialog(
        title = title,
        content = {
            LazyColumn {
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
                        Divider(color = BlueIsh)
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
    name: MutableState<String>,
    onConfirmClick: (String) -> Unit
) {
    ShowDialog(
        title = "Name this Recipe",
        content = {
            DefaultTextField(
                label = "name",
                value = name.value,
                onValueChange = {
                    name.value = it
                },
                maxCount = RECIPE_NAME_MAX_CHAR_COUNT
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirmClick(name.value)
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            Button(onClick = {
                name.value = ""
                isShowing.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        isShowing = isShowing
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
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
        scope.launch {
            val newRecipe = Recipe(recipe.recipe, newRecipeName.value, recipe.imageUrl)
            favRecipes[favRecipes.indexOf(recipe)] = newRecipe
            dao.updateRecipe(recipe = newRecipe)
            isRenameFieldShowing.value = false
            delay(300)
            isRenameLabelShowing.value = true
        }
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
                            value = newRecipeName.value,
                            onValueChange = {
                                newRecipeName.value = it
                            },
                            maxCount = RECIPE_NAME_MAX_CHAR_COUNT
                        )
                    }

                    GlideImage(
                        model = recipe.imageUrl,
                        contentDescription = "Recipe Image",
                        loading = placeholder(ColorPainter(Color.Gray)),
                    )

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

@Composable
fun RejectedRecipeHistoryDialog(
    isShowing: MutableState<Boolean>,
    recipes: List<Recipe>,
    onSelectItem: (Recipe) -> Unit,
) {
    ShowDialog(
        title = if (recipes.isEmpty()) NO_REJECTED_RECIPES_DIALOG_LABEL
        else REJECTED_RECIPES_DIALOG_LABEL,
        content = {
            LazyColumn {
                items(recipes.size) { index ->
                    val selectedRecipe = recipes.reversed()[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectItem(selectedRecipe)
                                isShowing.value = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            selectedRecipe.name,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    if (index != recipes.size - 1) {
                        Divider(color = BlueIsh)
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
fun RejectedRecipeContentDialog(
    dao: RecipeDao,
    recipe: Recipe,
    isShowing: MutableState<Boolean>
) {
    ShowDialog(
        title = recipe.name,
        content = {
            LazyColumn(content = {
                items(1) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

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
                }
            })
        },
        confirmButton = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_favorite_24),
                contentDescription = "Favorite",
                tint = BlueIsh,
                modifier = Modifier.clickable {
                    dao.insertRecipe(recipe = recipe)
                    isShowing.value = false
                })
        },
        dismissButton = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back",
                tint = BlueIsh,
                modifier = Modifier
                    .padding(end = 30.dp)
                    .clickable {
                        dao.insertRecipe(recipe = recipe)
                        isShowing.value = false
                    })
        },
        isShowing = isShowing
    )
}

@Composable
@Suppress("unused_parameter")
fun FolderContentDialog(
    dao: RecipeDao,
    folder: RecipeFolder,
    folders: SnapshotStateList<RecipeFolder>,
    isShowing: MutableState<Boolean>,
    onDelete: () -> Unit
) {

}

@Composable
fun EmptyPromptDialog(
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    ShowDialog(
        title = "Please provide a prompt",
        modifier = modifier,
        content = {
            Column {
                Text(
                    "The text field can not be empty.",
                    fontSize = 16.sp,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isShowing.value = false
                }) {
                Text(text = "Dismiss", color = Color.White)
            }
        },
        dismissButton = {},
        isShowing = isShowing
    )
}

@Composable
fun DeleteDialog(
    title: String,
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    ShowDialog(
        title = title,
        modifier = modifier,
        content = {
            Column {
                Text(
                    "This can't be undone.",
                    fontSize = 16.sp,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDeleteClick()
                    isShowing.value = false
                }) {
                Text(text = "Delete", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    isShowing.value = false
                }) {
                Text(text = "Cancel", color = Color.White)
            }
        },
        isShowing = isShowing
    )
}