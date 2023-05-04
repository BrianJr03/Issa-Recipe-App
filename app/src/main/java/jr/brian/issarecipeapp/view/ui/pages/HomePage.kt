package jr.brian.issarecipeapp.view.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.view.ui.components.PresetMealsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    Scaffold() {
        Column(modifier = Modifier.padding(it)) {
            Spacer(modifier = Modifier.height(15.dp))
            PresetMealsRow()
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}