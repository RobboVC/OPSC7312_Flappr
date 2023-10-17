package com.example.opsc7312_flappr.ui.home

import EBirdApiService
import LocationPermissionHelper
import Observations
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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.annotation.DrawableRes
import com.example.opsc7312_flappr.BuildConfig
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    //EBIRD GLOBAL VARIABLES - START
    //API KEY
    private val apiKey = "ql19oi7mpdd1"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val eBirdApi = retrofit.create(EBirdApiService::class.java)

    private val latitude = -33.0
    private val longitude = 18.0

    //EBIRD CLOBAL VARIABLES - END

    private lateinit var locationPermissionHelper: LocationPermissionHelper

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapView

        locationPermissionHelper = LocationPermissionHelper(WeakReference(requireActivity()))

        locationPermissionHelper.checkPermissions { onMapReady() }

        //mapView.invalidate()
        /*
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS
        ) { addAnnotationToMap(37.419974, -122.078053) }
        */
        return view
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
            //addAnnotationToMap(18.0,-33.0, R.drawable.pin_red, "beans")
            //addAnnotationToMap(19.0, -34.0, R.drawable.pin_red, "beans 2")
            setupGesturesListener()

            // Make the eBird API call
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val observations = eBirdApi.getRecentObservations(latitude, longitude, apiKey=apiKey)
                    handleObservations(observations)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleObservations(observations: List<Observations>) {
        Log.d("Debug", "Received observations: $observations")


            // Iterate through the list of observations and add annotations
            for (observation in observations) {
                // Extract relevant information from the observation
                val species = observation.comName
                val obsLongitude = observation.lng
                val obsLatitude = observation.lat

                Log.d("Debug", "Coordinates - Latitude: $latitude, Longitude: $longitude")
                Log.d("Debug", "Observation - Latitude: ${observation.lat}, Longitude: ${observation.lng}")

                // Add Mapbox annotation for each observation with a red pin icon
                addAnnotationToMap(obsLongitude, obsLatitude, R.drawable.pin_red, species)
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
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
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
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*
    private fun addAnnotationToMap(longitude: Double, latitude: Double) {
        val bitmap = bitmapFromDrawableRes(requireContext(), R.drawable.pin_red)
        if (bitmap != null) {
            val annotationApi = mapView.annotations
            if (annotationApi != null) {
                val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(bitmap)
                pointAnnotationManager.create(pointAnnotationOptions)
                Log.d("Debug", "Annotation created successfully")
            } else {
                Log.e("Error", "Annotations API is null")
            }
        } else {
            Log.e("Error", "Bitmap is null")
        }
    }
     */

    private fun addAnnotationToMap(longitude: Double, latitude: Double, iconResId: Int, species: String) {
        val bitmap = bitmapFromDrawableRes(requireContext(), iconResId)
        if (bitmap != null) {
            val annotationApi = mapView.annotations
            if (annotationApi != null) {
                val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(bitmap)
                    .withIconSize(1.0) // Adjust the size as needed
                    .withTextField(species) // Display species as text
                pointAnnotationManager.create(pointAnnotationOptions)
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

}