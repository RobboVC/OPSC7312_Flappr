package com.example.opsc7312_flappr.ui.species


import EBirdApiService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc7312_flappr.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import kotlin.coroutines.resume
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpeciesFragment : Fragment(){

    private lateinit var adapter: NotableBirdAdapter

    private lateinit var observations: List<NotableObservations>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude = -33.0
    private var longitude = 18.0

    private val apiKey = "ql19oi7mpdd1"
    private val eBirdApiService = RestClient2.create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val eBirdApi = retrofit.create(EBirdApiService::class.java)

    private lateinit var letterSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_species, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        longitude = EBirdApiServiceKotlin.startLong
        latitude = EBirdApiServiceKotlin.startLat

        // Initialize RecyclerView and adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotableBirdAdapter(emptyList()) // Pass an empty list initially
        recyclerView.adapter = adapter


        // Make the API call
        makeApiCall()


        return view
    }

    private fun makeApiCall() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                observations = eBirdApi.getNotableBirds(latitude, longitude, apiKey = apiKey)
                handleNotable(observations)

                setupSpinner()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupSpinner() {
        // Assuming you have an array of letters, update it based on your requirements
        val letters = observations.map { it.comName.first().toString() }.distinct().sorted()

        letterSpinner = view?.findViewById(R.id.spinnerLetters) ?: return

        letterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val filteredObservations = filterObservationsByLetter(letterSpinner.selectedItem.toString(), observations)
                adapter.updateData(filteredObservations)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }
    }

    private fun filterObservationsByLetter(letter: String, observations: List<NotableObservations>): List<NotableObservations> {
        return observations.filter { it.comName.startsWith(letter, ignoreCase = true) }
    }


    private fun handleNotable(observations: List<NotableObservations>) {
        Log.d("Debug", "Received observations: $observations")

        // Iterate through the list of observations and add annotations
        for (observation in observations) {
            // Extract relevant information from the observation
            val commonName = observation.comName
            val scientificName = observation.sciName
            val noteLongitude = observation.lng
            val noteLatitude = observation.lat

            Log.d("Debug", "Coordinates - Latitude: $latitude, Longitude: $longitude")
            Log.d(
                "Debug",
                "Observation - Latitude: ${noteLatitude}, Longitude: ${noteLongitude}"
            )

            // Add Mapbox annotation for each observation with a red pin icon

        }

        //updating recyclerview
        adapter.updateData(observations)
    }


}


//Toast.makeText(requireContext(), "Marker Clicked - Lat: $latitude, Long: $longitude", Toast.LENGTH_SHORT).show()
