package jr.brian.issarecipeapp.view.ui.components.swipe_cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.util.SwipeHeaderLabel

@Composable
fun StackBackgroundCard(
    modifier: Modifier = Modifier,
    dao: RecipeDao,
    state: SwipeableState,
    content: @Composable () -> Unit,
) = with(state) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        SwipeHeaderLabel(dao)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier
                .padding(bottom = 10.dp)
                .graphicsLayer {
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                }) {
            Box( contentAlignment = Alignment.Center) {
                content()
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}