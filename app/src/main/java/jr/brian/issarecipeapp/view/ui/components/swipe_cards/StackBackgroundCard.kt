package jr.brian.issarecipeapp.view.ui.components.swipe_cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun StackBackgroundCard(
    modifier: Modifier = Modifier,
    state: SwipeableState,
    content: @Composable () -> Unit,
) = with(state) {
    Card(
        modifier
            .padding(bottom = 10.dp)
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
            }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}