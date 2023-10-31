package jr.brian.issarecipeapp.view.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    maxCount: Int = Int.MAX_VALUE,
    onValueChange: ((str: String) -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val showErrorColor = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        modifier = modifier.padding(
            start = 15.dp,
            end = 15.dp,
            bottom = 15.dp
        ),
        value = value,
        onValueChange = { str ->
            if (str.length <= maxCount) {
                if (str.isNotBlank()) {
                    showErrorColor.value = false
                } else if (str.toIntOrNull() != null) {
                    showErrorColor.value = false
                }
            }
            onValueChange?.invoke(str)
        },
        label = {
            Text(
                text = label,
                style = TextStyle(
                    color = BlueIsh,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = if (showErrorColor.value) Color.Red else BlueIsh,
            unfocusedIndicatorColor = if (showErrorColor.value) Color.Red
            else MaterialTheme.colorScheme.background
        ),
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDone?.invoke()
        }),
    )
}