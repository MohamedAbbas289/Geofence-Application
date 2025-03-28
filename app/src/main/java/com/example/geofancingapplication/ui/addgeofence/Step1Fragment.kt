package com.example.geofancingapplication.ui.addgeofence

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geofancingapplication.R
import com.example.geofancingapplication.databinding.FragmentStep1Binding
import com.example.geofancingapplication.ui.addgeofence.viewmodels.Step1ViewModel
import com.example.geofancingapplication.viewmodels.SharedViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch


class Step1Fragment : Fragment() {
    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val step1ViewModel: Step1ViewModel by viewModels()

    private lateinit var geoCoder: Geocoder
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        geoCoder = Geocoder(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.sharedViewModel = sharedViewModel
        binding.step1ViewModel = step1ViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.step1Back.setOnClickListener {
            onStep1BackClicked()
        }
        getCountryCodeFromCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCountryCodeFromCurrentLocation() {
        lifecycleScope.launch {
            val placeField = listOf(Place.Field.LAT_LNG)
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeField)

            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    val latLng = response.placeLikelihoods[0].place.latLng!!
                    val address = geoCoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1
                    )
                    sharedViewModel.geoCountryCode = address!![0].countryCode
                    Log.e("Step1Fragment", sharedViewModel.geoCountryCode)
                    enabledNextButton()
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e(
                            "Step1Fragment",
                            "Exception: ${exception.statusCode}" + exception.message
                        )
                    }
                    enabledNextButton()
                }
            }
        }
    }

    private fun enabledNextButton() {
        if (sharedViewModel.geoName.isNotEmpty()) {
            step1ViewModel.enabledNextButton(true)
        }
    }

    private fun onStep1BackClicked() {
        findNavController().navigate(R.id.action_step1Fragment_to_mapsFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}