package me.likeavitoapp.screens.main.order.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import me.likeavitoapp.model.Order
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.main.order.ChatView
import kotlin.math.absoluteValue


@Composable
fun CreateOrderScreenProvider(screen: CreateOrderScreen) {
    CreateOrderScreenView(screen)

    BackHandler {
        screen.PressBack()
    }
}

@Composable
fun CreateOrderScreenView(screen: CreateOrderScreen) = with(screen) {
    // самовывоз
    //     адрес
    //     время
    //     посмотреть на карте
    // доставка
    //    компания доставки
    // оплатить
    // card number Номер карты
    // mm/yy Действует до
    // cvv/cvc три цифры с обратной стороны карты

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        with(state.ad) {
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
                    ActualAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        url = photoUrls[page]
                    )
                    AsyncImage(model = photoUrls[page], contentDescription = "$page")
                }
            }
        }
    }
}
