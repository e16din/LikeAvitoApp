package me.likeavitoapp.screens.main.tabs.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.R
import me.likeavitoapp.get
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme
import java.text.SimpleDateFormat

@Composable
fun OrderView(
    screen: OrdersScreen,
    order: Order,
    newMessagesCount: State<Int>
) {
    @Composable
    fun getOrderState(state: Order.State): String {
        return when (state) {
            Order.State.Init -> throw IllegalArgumentException("Use .Active or .Archived here")
            Order.State.Active -> stringResource(R.string.order_active_state_label)
            Order.State.Archived -> stringResource(R.string.done_order_status_label)
        }
    }
    Card(modifier = Modifier) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text =
                    buildAnnotatedString {
                        append(
                            stringResource(
                                R.string.order_title,
                                "${order.number} | "
                            )
                        )
                        val color = if (order.state == Order.State.Active)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.outline
                        withStyle(style = SpanStyle(color = color)) {
                            append(getOrderState(order.state))
                        }
                    },
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp, bottom = 12.dp)
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .clickable {
                        screen.ClickToAdUseCase(order)
                    }
            ) {
                ActualAsyncImage(
                    modifier = Modifier
                        .height(64.dp)
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                    url = order.ad.photoUrls.first(),
                    contentScale = ContentScale.FillHeight
                )

                Text(
                    text = order.ad.title,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .align(Alignment.CenterVertically),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val createdDate = remember {
                SimpleDateFormat("dd.MM.yyyy", Locale.current.platformLocale)
                    .format(order.createdMs)
            }
            val expectedArrivalDate = remember {
                SimpleDateFormat("dd.MM.yyyy", Locale.current.platformLocale)
                    .format(order.expectedArrivalMs)
            }

            Text(
                text = stringResource(R.string.order_created_label, createdDate),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            if (order.pickupPoint?.isInPlace == true) {
                Text(
                    text = stringResource(R.string.order_in_place_label),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )

            } else {
                Text(
                    text = stringResource(R.string.expected_arrival_label, expectedArrivalDate),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                IconButton(
                    onClick = {
                        val address = if (order.pickupPoint != null)
                            order.pickupPoint.address
                        else
                            order.ad.address?.data ?: ""
                        screen.ClickToAddressUseCase(address)
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        "location",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    if (order.pickupPoint != null) {
                        Text(
                            text = stringResource(R.string.pickup_point_address_label),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier,
                        )
                        Text(
                            text = "${order.ad.address?.data}",
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier,
                        )

                    } else {
                        Text(
                            text = stringResource(R.string.delivary_address_label),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier,
                        )
                        Text(
                            text = "${order.ad.address?.data}",
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(start = 16.dp, end = 8.dp, bottom = 12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable {
                        screen.ClickToMessagesUseCase(order)
                    }
            ) {
                Text(
                    text = if (newMessagesCount.value > 0)
                        stringResource(R.string.new_messages_label, newMessagesCount.value)
                    else
                        stringResource(R.string.move_to_chat_label),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier,
                )
                Icon(
                    modifier = Modifier.padding(start = 12.dp),
                    imageVector = Icons.Default.Email,
                    contentDescription = "messageIcon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderViewPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        val screen = OrdersScreen(navigator = mockScreensNavigator())
        val order = MockDataProvider().createOrder(0, Order.Type.Pickup)
        OrderView(
            order = order,
            screen = screen,
            newMessagesCount = remember { mutableStateOf(5) }
        )
    }
}

