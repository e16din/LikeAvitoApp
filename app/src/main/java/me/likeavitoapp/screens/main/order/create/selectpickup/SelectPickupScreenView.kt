package me.likeavitoapp.screens.main.order.create.selectpickup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import me.likeavitoapp.R
import me.likeavitoapp.isPreviewMode
import me.likeavitoapp.log
import me.likeavitoapp.get
import me.likeavitoapp.model.Order.PickupPoint
import me.likeavitoapp.model.Order.PickupPoint.Type.*
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.model.collectAsState
import me.likeavitoapp.model.mockMainSet
import me.likeavitoapp.model.mockScreensNavigator
import me.likeavitoapp.screens.ActionTopBar
import me.likeavitoapp.screens.Chip
import me.likeavitoapp.ui.theme.LikeAvitoAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPickupScreenProvider(screen: SelectPickupScreen) {

    Surface(modifier = Modifier.fillMaxSize()) {
        ActionTopBar(
            title = stringResource(R.string.select_pickup_point),
            onClose = {
                screen.ClickToCloseUseCase()
            },
            onDone = {
                screen.ClickToDoneUseCase()
            },
        ) { innerPadding ->
            SelectPickupScreenView(screen, Modifier.padding(innerPadding))
        }
    }

    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        onDispose {
            MapKitFactory.getInstance().onStop()
        }
    }

    BackHandler {
        screen.PressBackUseCase()
    }
}

@Composable
fun SelectPickupScreenView(screen: SelectPickupScreen, modifier: Modifier) = with(screen) {
    val query by screen.state.query.collectAsState()
    val pickupPointType by screen.state.pickupPointType.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        val addressText by screen.state.query.collectAsState()
        val suggestions by screen.state.suggestions.output.collectAsState()

        Column {
            TextField(
                value = query,
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

            fun getPickupTypeName(type: PickupPoint.Type): String {
                return when (type) {
                    Post -> "Почта России"
                    Cdek -> "CDEK"
                    Boxberry -> "Boxberry"
                    OwnerAddress -> "Адрес продавца"
                }
            }

            Row {
                entries.forEach { type ->
                    Chip(
                        startIcon = {
                            if (pickupPointType == type) Icons.Default.Check else null
                        },
                        startIconTint = Color.Black.copy(alpha = 0.5f),
                        contentDescription = getPickupTypeName(type),
                        label = getPickupTypeName(type),
                        isClickable = true,
                        onClick = {
                            screen.SelectPickupPointTypeUseCase(type)
                        }
                    )
                }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                if (suggestions.isNotEmpty()) {
                    LazyColumn {
                        items(suggestions) { suggestion ->
                            Text(
                                text = suggestion.name,
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
                    YandexMapView(screen)
                }
            }
        }
    }
}


@Composable
fun YandexMapView(screen: SelectPickupScreen) {
    val mapKit = remember { MapKitFactory.getInstance() }
    val locationManager = remember { mapKit.createLocationManager() }

    DisposableEffect(Unit) {
        mapKit.onStart()

        onDispose {
            mapKit.resetLocationManagerToDefault()
            mapKit.onStop()
        }
    }

    var actualMap by remember { mutableStateOf<Map?>(null) }

    RequestLocationPermission(
        onPermissionGranted = {
            mapKit.setLocationManager(locationManager)
            val locationListener = object : LocationListener {
                override fun onLocationUpdated(location: com.yandex.mapkit.location.Location) {
                    screen.ChangeAreaPointUseCase(location.position)
                }

                override fun onLocationStatusUpdated(status: LocationStatus) {
                }
            }
            locationManager.requestSingleUpdate(locationListener)

        },
        onPermissionDenied = {
            screen.PressBackUseCase()
        }
    )

    fun moveTo(position: Point) {
        val zoom = 18f
        val azimuth = 150f
        val tilt = 30f
        log("move: ${position}")
        actualMap?.move(
            CameraPosition(
                position,
                zoom,
                azimuth,
                tilt
            )
        )
    }

    val areaPoint = screen.state.areaPoint.collectAsState()
    LaunchedEffect(areaPoint) {
        moveTo(areaPoint.value)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                actualMap = mapWindow.map
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
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
    fun listenCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            log("location: $location")
            onLocationReceived(location)
        }.addOnFailureListener { throwable ->
            throwable.log()
            onLocationReceived(null)
        }
    }
}

@Preview
@Composable
fun SelectPickupScreenPreview() {
    get = mockMainSet()
    LikeAvitoAppTheme {
        SelectPickupScreenProvider(
            screen = SelectPickupScreen(
                selectedPickupPoint = UpdatableState(
                    PickupPoint(
                        id = 0,
                        address = "г.Москва, пр-т.Ленина, д.48",
                        openingHoursFrom = 8,
                        openingHoursTo = 21,
                        point = PickupPoint.Point(0.0, 0.0),
                        isInPlace = true
                    )
                ),
                navigator = mockScreensNavigator(),
            )
        )
    }
}