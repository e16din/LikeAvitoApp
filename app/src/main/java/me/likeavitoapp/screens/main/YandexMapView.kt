package me.likeavitoapp.screens.main


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import me.likeavitoapp.log
import me.likeavitoapp.model.Order

@Composable
fun YandexMapView(
    points: List<Order.PickupPoint>,
    areaPoint: State<Point>,
    locationListener: LocationListener,
    onPermissionDenied: () -> Unit
) {
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
            locationManager.requestSingleUpdate(locationListener)

        },
        onPermissionDenied = onPermissionDenied
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