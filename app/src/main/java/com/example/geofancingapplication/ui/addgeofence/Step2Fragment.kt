package com.example.geofancingapplication.ui.addgeofence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofancingapplication.R
import com.example.geofancingapplication.databinding.FragmentStep2Binding
import com.example.geofancingapplication.ui.addgeofence.adapters.PredictionsAdapter
import com.example.geofancingapplication.ui.addgeofence.viewmodels.Step2ViewModel
import com.example.geofancingapplication.util.NetworkListener
import com.example.geofancingapplication.util.hide
import com.example.geofancingapplication.viewmodels.SharedViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class Step2Fragment : Fragment() {
    private var _binding: FragmentStep2Binding? = null
    private val binding get() = _binding!!

    private val predictionAdapter by lazy { PredictionsAdapter() }

    private val sharedVM: SharedViewModel by activityViewModels()
    private val step2VM: Step2ViewModel by viewModels()

    private lateinit var placesClient: PlacesClient

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep2Binding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedVM
        binding.step2ViewModel = step2VM
        binding.lifecycleOwner = this

        checkInternetConnection()

        binding.predictionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.predictionsRecyclerView.adapter = predictionAdapter

        binding.geofenceLocationEt.doOnTextChanged { text, _, _, _ ->
            handleNextButton(text)
            getPlaces(text)
        }

        binding.step2Back.setOnClickListener {
            findNavController().navigate(R.id.action_step2Fragment_to_step1Fragment)
        }

        binding.step2Next.setOnClickListener {
            findNavController().navigate(R.id.action_step2Fragment_to_step3Fragment)
        }

        subscribeToObservers()

        return binding.root
    }

    private fun handleNextButton(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            step2VM.enabledNextButton(false)
        }
    }

    private fun subscribeToObservers() {
        lifecycleScope.launch {
            predictionAdapter.placeId.collectLatest { placeId ->
                if (placeId.isNotEmpty()) {
                    onCitySelected(placeId)
                }
            }
        }
    }

    private fun onCitySelected(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.DISPLAY_NAME
        )
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                sharedVM.geoLatLng = response.place.latLng!!
                sharedVM.geoLocationName = response.place.name!!
                sharedVM.geoCitySelected = true
                binding.geofenceLocationEt.setText(sharedVM.geoLocationName)
                binding.geofenceLocationEt.setSelection(sharedVM.geoLocationName.length)
                binding.predictionsRecyclerView.hide()

                Log.d("Step2fragment", sharedVM.geoLatLng.toString())
                Log.d("Step2fragment", sharedVM.geoLocationName)
                Log.d("Step2fragment", sharedVM.geoCitySelected.toString())

                step2VM.enabledNextButton(true)
            }
            .addOnFailureListener { exception ->
                Log.e("Step2Fragment", exception.message.toString())
            }
    }

    private fun getPlaces(text: CharSequence?) {
        if (sharedVM.checkDeviceLocationSettings(requireContext())) {
            lifecycleScope.launch {
                if (text.isNullOrEmpty()) {
                    predictionAdapter.setData(emptyList())
                } else {
                    val token = AutocompleteSessionToken.newInstance()

                    val request =
                        FindAutocompletePredictionsRequest.builder()
                            .setCountries(sharedVM.geoCountryCode)
                            .setTypeFilter(TypeFilter.CITIES)
                            .setSessionToken(token)
                            .setQuery(text.toString())
                            .build()
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            predictionAdapter.setData(response.autocompletePredictions)
                            binding.predictionsRecyclerView.scheduleLayoutAnimation()
                        }
                        .addOnFailureListener { exception: Exception? ->
                            if (exception is ApiException) {
                                Log.e("Step2Fragment", exception.statusCode.toString())
                            }
                        }
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please Enable Location Settings.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkInternetConnection() {
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.startListening(requireContext())
                .collect { online ->
                    Log.d("Internet", online.toString())
                    step2VM.setInternetAvailable(online)
                    if (online && sharedVM.geoCitySelected) {
                        step2VM.enabledNextButton(true)
                    } else {
                        step2VM.enabledNextButton(false)
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkListener.stopListening(requireContext())
        _binding = null
    }
}