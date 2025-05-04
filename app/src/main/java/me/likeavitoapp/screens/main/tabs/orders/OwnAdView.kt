package me.likeavitoapp.screens.main.tabs.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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
import me.likeavitoapp.R
import me.likeavitoapp.developer.primitives.Colors
import me.likeavitoapp.get
import me.likeavitoapp.mocks.MockDataProvider
import me.likeavitoapp.model.Order
import me.likeavitoapp.model.OwnAd
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActualAsyncImage
import me.likeavitoapp.screens.CheckBoxLabel
import me.likeavitoapp.ui.theme.AppTypography
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme
import java.text.SimpleDateFormat

@Composable
fun OwnAdView(
    screen: OrdersScreen,
    ownAd: OwnAd,
    newMessagesCount: State<Int>
) {
    @Composable
    fun getStateName(state: Int): String {
        return when (state) {
            0 -> stringResource(R.string.order_active_state_label)
            1 -> stringResource(R.string.done_order_status_label)
            else -> throw IllegalArgumentException("Use 0 or 1 here")
        }
    }

    val isActive = ownAd.isActive()
    Card(
        modifier = Modifier
            .padding(top = 4.dp, start = 4.dp, end = 4.dp)
            .border(if (isActive) 2.dp else 0.dp, color = Colors.Green)
    ) {
        Column(
            Modifier.background(
                if (isActive)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text =
                    buildAnnotatedString {
                        append(
                            stringResource(
                                R.string.own_ad_title,
                                "${ownAd.id} | "
                            )
                        )
                        val color = if (isActive)
                            Colors.Green
                        else
                            MaterialTheme.colorScheme.outline
                        withStyle(style = SpanStyle(color = color)) {
                            append(getStateName(ownAd.state))
                        }
                    },
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Box {
                Row(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, bottom = 12.dp)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .clickable {
                            screen.ClickToOwnAdUseCase(ownAd)
                        }
                ) {
                    ActualAsyncImage(
                        modifier = Modifier
                            .height(64.dp)
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                        url = ownAd.photoUrls.first(),
                        contentScale = ContentScale.FillHeight
                    )

                    Text(
                        text = ownAd.title!!,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterVertically),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    stringResource(R.string.edit_own_ad_button),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 20.dp, end = 12.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

            }

            val createdDate = remember {
                SimpleDateFormat("dd.MM.yyyy", Locale.current.platformLocale)
                    .format(ownAd.createdMs)
            }

            Text(
                text = stringResource(R.string.own_ad_created_label, createdDate),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            if (!ownAd.address.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.own_ad_order_address_label, ownAd.address!!),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            CheckBoxLabel(
                label = stringResource(R.string.autoupdate_checkbox),
                checked = ownAd.isAutoupdateEnabled,
                enabled = false
            )
            CheckBoxLabel(
                label = stringResource(R.string.premium_checkbox),
                checked = ownAd.isPremiumEnabled,
                enabled = false
            )
            CheckBoxLabel(
                label = stringResource(R.string.bargaining_enabled_checkbox),
                checked = ownAd.isBargainingEnabled,
                enabled = false
            )

            if (isActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    val newMessagesCounters by get.sources().app.totalNewMessagesCount.collectAsState()

                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp, bottom = 12.dp, top = 16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                screen.ClickToOwnAdMessagesUseCase(ownAd)
                            }
                    ) {
                        Text(
                            text = if (newMessagesCount.value > 0)
                                stringResource(R.string.new_messages_label, newMessagesCount.value)
                            else
                                stringResource(R.string.move_to_chat_button),
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

                    if (newMessagesCounters.size > 0) {
                        val pair = newMessagesCounters.firstOrNull { it.first == ownAd.id }
                        val count = pair?.second ?: 0
                        if (count > 0) {
                            Text(
                                "$count",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(end = 2.dp, top = 8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnAdViewPreview() {
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

