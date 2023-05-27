package jr.brian.issarecipeapp.view.ui.components.swipe_cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.util.SWIPE_SCREEN_LABEL
import jr.brian.issarecipeapp.util.SwipeHeaderLabel
import jr.brian.issarecipeapp.util.getPath
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh

@Composable
fun StackForegroundCard(
    modifier: Modifier = Modifier,
    state: SwipeableState,
    content: @Composable () -> Unit,
) {
    SwipeOverlay(state) {
        BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                SwipeHeaderLabel()

                Spacer(modifier = Modifier.height(20.dp))

                Card(Modifier.swipeable(state)) {
                    Box(contentAlignment = Alignment.Center) {
                        content()
                    }
                }

                SwipeControls(this@BoxWithConstraints.constraints.maxWidth, state)
            }

        }
    }
}