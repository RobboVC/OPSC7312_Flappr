package com.example.opsc7312_flappr.ui.sightings

import EBirdApiService
import EBirdApiServiceKotlin
import LocationPermissionHelper
import Observations
import UserObservation
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.opsc7312_flappr.databinding.FragmentHomeBinding
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.opsc7312_flappr.R
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.opsc7312_flappr.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.core.app.ActivityCompat
import com.example.opsc7312_flappr.Login
import com.example.opsc7312_flappr.databinding.FragmentSightingsBinding
import com.example.opsc7312_flappr.ui.home.Navigation
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation


class SightingsFragment : Fragment() {

    private var _binding: FragmentSightingsBinding? = null

    //EBIRD GLOBAL VARIABLES - START
    //API KEY
    private val apiKey = "ql19oi7mpdd1"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val eBirdApi = retrofit.create(EBirdApiService::class.java)

    private var latitude = -33.0
    private var longitude = 18.0

    //EBIRD GLOBAL VARIABLES - END

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    private lateinit var mapView: MapView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSightingsBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        requestLocation()

        locationPermissionHelper = LocationPermissionHelper(WeakReference(requireActivity()))

        locationPermissionHelper.checkPermissions { onMapReady() }


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        } else {
            // Permission is granted, get current location
            requestLocation()
        }

        return view
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Set latitude and longitude
                    latitude = location.latitude
                    longitude = location.longitude

                    // Now you can use latitude and longitude as needed
                }
            }
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
            handleObservations(EBirdApiServiceKotlin.listOfUserObservations)

            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)

            pointAnnotationManager.addClickListener { annotation ->
                if (annotation is PointAnnotation) {
                    val latitude = annotation.point.latitude()
                    val longitude = annotation.point.longitude()

                    Log.d("Debug", "Marker Clicked - Latitude: $latitude, Longitude: $longitude")

                    // Display a toast for debugging
                    Toast.makeText(
                        requireContext(),
                        "Marker Clicked - Lat: $latitude, Long: $longitude",
                        Toast.LENGTH_SHORT
                    ).show()

                    displayMarkerInfo(latitude, longitude)
                    true // Return true to indicate that the click event has been handled
                } else {
                    false // Return false if it's not a PointAnnotation
                }
            }
        }
    }

    private fun handleObservations(observations: List<UserObservation>) {
        Log.d("Debug", "Received observations: $observations")


        // Iterate through the list of observations and add annotations
        for (UserObservation in observations) {
            // Extract relevant information from the observation
            val species = UserObservation.name
            val obsLongitude = UserObservation.longitude
            val obsLatitude = UserObservation.latitude

            Log.d("Debug but blue", "Coordinates - Latitude: $latitude, Longitude: $longitude")
            Log.d(
                "Debug",
                "Observation - Latitude: ${UserObservation.latitude}, Longitude: ${UserObservation.longitude}"
            )

            // Add Mapbox annotation for each observation with a red pin icon
            addAnnotationToMap(obsLongitude, obsLatitude, R.drawable.pin_blue, species)
        }

    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.profile_icon,
                ),

                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                requestLocation()
            } else {
                // Handle case where user denied location permission
                // You may want to show a message to the user or take other action
            }
        } else {
            // Pass the result to your existing locationPermissionHelper
            locationPermissionHelper.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }

    private fun addAnnotationToMap(
        longitude: Double,
        latitude: Double,
        iconResId: Int,
        species: String
    ) {
        val bitmap = bitmapFromDrawableRes(requireContext(), iconResId)
        if (bitmap != null) {
            val annotationApi = mapView.annotations
            if (annotationApi != null) {
                val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(bitmap)
                    .withIconSize(1.0) // Adjust the size as needed
                    .withTextField(species)
                // Display species as text
                pointAnnotationManager.create(pointAnnotationOptions)
                pointAnnotationManager?.addClickListener {
                    Toast.makeText(
                        requireContext(),
                        "Latitude$latitude" + "Longitude$longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(requireContext(), Navigation::class.java)
                    EBirdApiServiceKotlin.long = longitude
                    EBirdApiServiceKotlin.lat = latitude
                    startActivity(intent)
                    false
                }
                Log.d("Debug", "Annotation created successfully")
            } else {
                Log.e("Error", "Annotations API is null")
            }
        } else {
            Log.e("Error", "Bitmap is null")
        }
    }


    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId)).also {

        }

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun displayMarkerInfo(latitude: Double, longitude: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder
            .setTitle("Marker Information")
            .setMessage("Latitude: $latitude\nLongitude: $longitude")
            .setPositiveButton("OK", null)
            .show()
    }

}


