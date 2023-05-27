package jr.brian.issarecipeapp.view.ui.components.swipe_cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jr.brian.issarecipeapp.view.ui.theme.dp_10
import jr.brian.issarecipeapp.view.ui.theme.dp_15
import jr.brian.issarecipeapp.view.ui.theme.sp_16

@Composable
fun RecipeCard(
    quote: String,
    modifier: Modifier = Modifier,
    cardPadding: PaddingValues = PaddingValues(12.dp)
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.90f)
            .fillMaxHeight(0.80f)
            .padding(2.dp),
        shape = RoundedCornerShape(dp_10),
        border = BorderStroke(0.5.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(dp_15),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(cardPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = quote,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = sp_16,
                        lineHeight = sp_16
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuoteCardPreview() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        RecipeCard(
            "The power of water is its ability to take any shape..."
        )
    }
}