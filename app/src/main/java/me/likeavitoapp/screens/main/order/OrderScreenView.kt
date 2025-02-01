package me.likeavitoapp.screens.main.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import me.likeavitoapp.model.Order
import kotlin.math.absoluteValue


@Composable
fun OrderScreenProvider() {
}

@Composable
fun OrderScreenView(screen: OrderScreen) = with(screen) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        with(state.order.ad) {
            Text(title)

            val pagerState = rememberPagerState(pageCount = { photoUrls.size })
            HorizontalPager(state = pagerState) { page ->
                Card(
                    Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                ) {
                    AsyncImage(model = photoUrls[page], contentDescription = "$page")
                }
            }
        }

        if (state.order.state == Order.State.Active) {
            ChatView()
        }
    }
}
