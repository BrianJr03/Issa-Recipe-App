package jr.brian.issarecipeapp.view.ui.pages

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.view.ui.components.LottieRecipe
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavToMealDetails: () -> Unit,
    onNavToFavRecipes: () -> Unit
) {
    val context = LocalContext.current

    Scaffold() {
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                fontSize = 20.sp,
                color = BlueIsh,
                text = stringResource(id = R.string.app_name),
            )

            Spacer(modifier = Modifier.height(30.dp))

            LottieRecipe(
                isShowing = remember {
                    mutableStateOf(true)
                }, modifier = Modifier.size(250.dp)
            )

            Text(
                fontSize = 30.sp,
                color = BlueIsh,
                text = "Generate",
                modifier = Modifier.clickable {
                    onNavToMealDetails()
                })

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                fontSize = 30.sp,
                color = BlueIsh,
                text = "Favorites",
                modifier = Modifier.clickable {
                    onNavToFavRecipes()
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                fontSize = 30.sp,
                color = BlueIsh,
                text = "Settings",
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}