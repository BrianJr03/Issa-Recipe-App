package jr.brian.issarecipeapp.view.ui.pages

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import jr.brian.issarecipeapp.model.local.Recipe
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.local.RecipeFolder
import jr.brian.issarecipeapp.view.ui.components.DefaultTextField
import jr.brian.issarecipeapp.view.ui.components.FolderContentDialog
import jr.brian.issarecipeapp.view.ui.components.RecipeContentDialog
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun FavRecipesPage(dao: RecipeDao) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pagerState.currentPage == 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                } else {
                    isEnabled = false
                    backPressedDispatcher?.onBackPressed()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        backPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { currentPageIndex ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(15.dp))

                    when (currentPageIndex) {
                        0 -> {
                            RecipeGrid(
                                dao = dao,
                                focusManager = focusManager,
                                interactionSource = interactionSource
                            )
                        }

                        1 -> {
                            FoldersComingSoon(
                                focusManager = focusManager,
                                interactionSource = interactionSource
                            )
//                            FoldersGrid(
//                                dao = dao,
//                                focusManager = focusManager,
//                                interactionSource = interactionSource
//                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = BlueIsh,
                inactiveColor = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeGrid(
    dao: RecipeDao,
    focusManager: FocusManager,
    interactionSource: MutableInteractionSource
) {
    val recipes = remember {
        dao.getRecipes().toMutableStateList()
    }

    val recipeQuery = remember {
        mutableStateOf("")
    }

    val filteredRecipes = remember(recipeQuery, recipes) {
        derivedStateOf {
            recipes.filter { recipe ->
                recipe.name.contains(recipeQuery.value, ignoreCase = true)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        DefaultTextField(
            label = "Search Recipes",
            value = recipeQuery.value,
            onValueChange = {
                recipeQuery.value = it
            },
            modifier = Modifier.padding(top = 15.dp)
        )

        if (filteredRecipes.value.isEmpty()) {
            Text("No Favorites", color = BlueIsh, fontSize = 20.sp)
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalArrangement = Arrangement.Center
            ) {
                items(filteredRecipes.value.size) { index ->
                    val recipe = filteredRecipes.value.reversed()[index]
                    RecipeBox(
                        dao = dao,
                        recipe = recipe,
                        favRecipes = recipes
                    )
                }
            }
        }
    }
}

@Composable
fun FoldersComingSoon(
    focusManager: FocusManager,
    interactionSource: MutableInteractionSource
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Text(
            text = "Folder Support Coming Soon.",
            style = TextStyle(fontSize = 20.sp, color = BlueIsh)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("unused")
@Composable
fun FoldersGrid(
    dao: RecipeDao,
    focusManager: FocusManager,
    interactionSource: MutableInteractionSource
) {
    val folders = remember {
        dao.getFolders().toMutableStateList()
    }

    val folderQuery = remember {
        mutableStateOf("")
    }

    val filteredFolders = remember(folderQuery, folders) {
        derivedStateOf {
            folders.filter { folder ->
                folder.name.contains(folderQuery.value, ignoreCase = true)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        DefaultTextField(
            label = "Search Folders",
            value = folderQuery.value,
            onValueChange = {
                folderQuery.value = it
            },
            modifier = Modifier.padding(top = 15.dp)
        )

        Button(
            onClick = {
                val e = RecipeFolder("test2", listOf())
                dao.insertFolder(e)
                folders.add(e)
            }
        ) {
            Text(text = "Create Folder")
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (filteredFolders.value.isEmpty()) {
            Text("No Folders", color = BlueIsh, fontSize = 20.sp)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(filteredFolders.value.size) { index ->
                        val folder = filteredFolders.value.reversed()[index]
                        FolderBox(
                            dao = dao,
                            folder = folder,
                            folders = folders
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeBox(
    dao: RecipeDao,
    recipe: Recipe,
    favRecipes: SnapshotStateList<Recipe>,
    modifier: Modifier = Modifier
) {
    val isShowingRecipe = remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    RecipeContentDialog(
        dao = dao,
        recipe = recipe,
        favRecipes = favRecipes,
        isShowing = isShowingRecipe,
    ) {
        scope.launch {
            isShowingRecipe.value = false
            delay(300)
            favRecipes.remove(recipe)
        }
    }

    Box(modifier = modifier
        .padding(16.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(BlueIsh)
        .clickable {
            isShowingRecipe.value = !isShowingRecipe.value
        }) {
        Text(
            text = recipe.name,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

    }
}

@Composable
fun FolderBox(
    dao: RecipeDao,
    folder: RecipeFolder,
    folders: SnapshotStateList<RecipeFolder>,
    modifier: Modifier = Modifier
) {
    val isShowingFolder = remember {
        mutableStateOf(false)
    }

    val isToBeDeleted = remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    FolderContentDialog(
        dao = dao,
        folder = folder,
        folders = folders,
        isShowing = isShowingFolder,
    ) {
        scope.launch {
            isToBeDeleted.value = true
            folders.remove(folder)
            delay(500)
            isToBeDeleted.value = false
            isShowingFolder.value = false
        }
    }

    AnimatedVisibility(visible = !isToBeDeleted.value) {
        Box(modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BlueIsh)
            .clickable {
                isShowingFolder.value = !isShowingFolder.value
            }) {
            Text(
                text = folder.name,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}