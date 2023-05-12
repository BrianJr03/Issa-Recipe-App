package jr.brian.issarecipeapp.view.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jr.brian.issarecipeapp.R

@Composable
private fun Lottie(
    @RawRes lottieRes: Int,
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val isPlaying by remember { mutableStateOf(isShowing.value) }
    val speed by remember { mutableStateOf(1f) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieRes)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    LottieAnimation(
        composition,
        { progress },
        modifier = modifier
    )
}

@Composable
private fun Lottie(
    @RawRes lottieRes: Int,
    isShowing: State<Boolean>,
    modifier: Modifier = Modifier
) {
    val isPlaying by remember { mutableStateOf(isShowing.value) }
    val speed by remember { mutableStateOf(1f) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieRes)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    LottieAnimation(
        composition,
        { progress },
        modifier = modifier
    )
}

@Suppress("Unused")
@Composable
fun LottieLoading(
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.loadingpink,
        isShowing = isShowing,
        modifier = modifier
    )
}

@Suppress("Unused")
@Composable
fun LottieLoading(
    isShowing: State<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.loadingpink,
        isShowing = isShowing,
        modifier = modifier
    )
}

@Suppress("Unused")
@Composable
fun LottieFoodBowl(
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.foodbowl,
        isShowing = isShowing,
        modifier = modifier
    )
}

@Suppress("Unused")
@Composable
fun LottieFoodBowl(
    isShowing: State<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.foodbowl,
        isShowing = isShowing,
        modifier = modifier
    )
}

@Composable
fun LottieRecipe(
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.recipe,
        isShowing = isShowing,
        modifier = modifier
    )
}

@Suppress("Unused")
@Composable
fun LottieRecipe(
    isShowing: State<Boolean>,
    modifier: Modifier = Modifier
) {
    Lottie(
        lottieRes = R.raw.recipe,
        isShowing = isShowing,
        modifier = modifier
    )
}
