package jr.brian.issarecipeapp.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.R
import jr.brian.issarecipeapp.model.local.PresetMeal
import jr.brian.issarecipeapp.model.local.presetMeals
import jr.brian.issarecipeapp.view.ui.theme.BlueIsh
import jr.brian.issarecipeapp.view.ui.theme.PinkIsh

@Composable
fun PresetMealsRow(onItemClick: (PresetMeal) -> Unit) {
    LazyRow(content = {
        items(presetMeals.size) { index ->
            val presetMeal = presetMeals[index]
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueIsh)
                    .clickable {
                        onItemClick(presetMeal)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_fastfood_24),
                        contentDescription = "",
                        modifier = Modifier.size(75.dp),
                        tint = PinkIsh
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(text = presetMeal.name)
                }
            }
        }
    })
}

