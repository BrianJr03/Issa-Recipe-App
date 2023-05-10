package jr.brian.issarecipeapp.view.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.view.ui.pages.DetailTextField

@Composable
private fun ShowDialog(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.White,
    backgroundColor: Color = Color.DarkGray,
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
            containerColor = backgroundColor,
            titleContentColor = titleColor
        )
    }
}

@Composable
fun ShowNameRecipeDialog(
    isShowing: MutableState<Boolean>,
    isShowingErrorColor: MutableState<Boolean>,
    name: MutableState<String>,
    onConfirmClick: () -> Unit
) {
    ShowDialog(
        title = "Name this Recipe",
        content = {
            DetailTextField(
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