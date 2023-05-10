package jr.brian.issarecipeapp.view.ui.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.customTextSelectionColors
import jr.brian.issarecipeapp.view.ui.pages.DefaultTextField

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
                isShowingErrorColor = isShowingErrorColor
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
    isShowing: MutableState<Boolean>,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    ShowDialog(
        title = recipe.name,
        content = {
            LazyColumn(content = {
                items(1) {
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
                    })
        },
        dismissButton = {
            Text(
                "Delete",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .combinedClickable(
                        onClick = {
                            Toast
                                .makeText(
                                    context,
                                    "Long-press to confirm",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                        onLongClick = {
                            isShowing.value = false
                            dao.removeRecipe(recipe = recipe)
                            onDelete()
                        })
            )
        },
        isShowing = isShowing
    )
}