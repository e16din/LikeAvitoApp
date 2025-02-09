package me.likeavitoapp.screens.main.order.create.selectpickup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import me.likeavitoapp.R
import me.likeavitoapp.collectAsState
import me.likeavitoapp.isPreviewMode
import me.likeavitoapp.model.PickupPoint
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.mockCoroutineScope
import me.likeavitoapp.model.mockDataSource
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.provideApp
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@Composable
fun SelectPickupScreenProvider(screen: SelectPickupScreen) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SelectPickupScreenView(screen)
    }

    BackHandler {
        screen.PressBack()
    }
}

@Composable
fun SelectPickupScreenView(screen: SelectPickupScreen) = with(screen) {
    val query = screen.state.query.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        val addressText by screen.state.query.collectAsState()
        val suggestions by screen.state.suggestions.collectAsState()

        Column {
            TextField(
                value = query.value,
                onValueChange = { newText ->
                    screen.ChangeQueryUseCase(newText)
                },
                label = { Text(stringResource(R.string.enter_address_label)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (!addressText.isNotEmpty()) {
                        IconButton(onClick = {
                            screen.ClickToClearAddress()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (suggestions.isNotEmpty()) {
                    LazyColumn {
                        items(suggestions) { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        screen.ClickToSelectSuggestion(suggestion)
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                if (!isPreviewMode()) {
                    YandexMapView()
                }
            }
        }
    }
}

@Composable
fun YandexMapView() {
    val mapKit = remember { MapKitFactory.getInstance() }
    DisposableEffect(Unit) {
        mapKit.onStart()
        onDispose {
            mapKit.onStop()
        }
    }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    RequestLocationPermission(
        onPermissionGranted = {
            val locationTracker = LocationTracker(context)
            locationTracker.getCurrentLocation { location ->
                currentLocation = location
            }
        },
        onPermissionDenied = {
            // Обработка отказа в разрешении
        }
    )
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            MapView(context).apply {
                currentLocation?.let {
                    mapWindow.map.move(
                        CameraPosition(Point(it.latitude, it.longitude), 1f, 0f, 0f)
                    )
                }
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
//            view.selectedItem = selectedItem
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }

    if (locationPermissionsState.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}

class LocationTracker(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onLocationReceived(location)
        }.addOnFailureListener {
            onLocationReceived(null)
        }
    }
}

@Preview
@Composable
fun SelectPickupScreenPreview() {
//    MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
//    MapKitFactory.initialize(LocalContext.current)

    LikeAvitoAppTheme {
        val scope = mockCoroutineScope()
        SelectPickupScreenProvider(
            screen = SelectPickupScreen(
                selectedPickupPoint = UpdatableState(
                    PickupPoint(
                        id = 0,
                        address = "г.Москва, пр-т.Ленина, д.48",
                        openingHoursFrom = 8,
                        openingHoursTo = 21
                    )
                ),
                parentNavigator = mockScreensNavigator(),
                scope = scope,
                sources = mockDataSource()
            )
        )
    }
}