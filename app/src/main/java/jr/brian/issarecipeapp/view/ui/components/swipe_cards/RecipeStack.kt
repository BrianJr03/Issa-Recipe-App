package jr.brian.issarecipeapp.view.ui.components.swipe_cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import jr.brian.issarecipeapp.model.local.RecipeDao

@Composable
fun <T> RecipeStack(
    modifier: Modifier = Modifier,
    dao: RecipeDao,
    items: InfiniteList<T>,
    onReject: (T) -> Unit = {},
    onLike: (T) -> Unit = {},
    itemContent: @Composable (T) -> Unit,
) {
    var currentPageNum by remember{ mutableStateOf(0) }

    val swipeState = rememberSwipeableState(
        onLeft = { items.current?.let{
            onReject(it)
            currentPageNum = items.moveToNext()
        } },
        onRight = { items.current?.let{
            onLike(it)
            currentPageNum = items.moveToNext()
        } }
    )

    items[currentPageNum+1]?.let { profile ->
        StackBackgroundCard(modifier, dao, swipeState) { itemContent(profile) }
    }
    items[currentPageNum]?.let { profile ->
        StackForegroundCard(modifier, dao, swipeState) { itemContent(profile) }
    }
}