package com.example.geofancingapplication.ui.maps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geofancingapplication.R
import com.example.geofancingapplication.databinding.FragmentMapsBinding
import com.example.geofancingapplication.util.Permissions.hasBackgroundLocationPermission
import com.example.geofancingapplication.util.Permissions.requestBackgroundLocationPermission
import com.example.geofancingapplication.util.hide
import com.example.geofancingapplication.util.show
import com.example.geofancingapplication.viewmodels.SharedViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        initViews()
    }

    private fun initViews() {
        binding.addGeofenceFab.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_add_geofence_graph)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
        map.isMyLocationEnabled = true
        map.setOnMapLongClickListener(this)
        map.uiSettings.apply {
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = true
        }
        onGeofenceReady()
    }

    private fun onGeofenceReady() {
        if (sharedViewModel.geofenceReady) {
            sharedViewModel.geofencePrepared = true
            sharedViewModel.geofenceReady = false
            displayInfoMessage()
            zoomToSelectedLocation()
        }
    }


    private fun displayInfoMessage() {
        lifecycleScope.launch {
            binding.infoMessageTextView.show()
            delay(2000)
            binding.infoMessageTextView.animate().alpha(0f).duration = 800
            delay(1000)
            binding.infoMessageTextView.hide()
        }
    }

    private fun zoomToSelectedLocation() {
        map.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(
                    sharedViewModel.geoLatLng, 10f
                ),
            2000, null
        )
    }

    override fun onMapLongClick(location: LatLng) {
        if (hasBackgroundLocationPermission(requireContext())) {
            if (sharedViewModel.geofencePrepared) {
                setupGeofence(location)
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to create a new Geofence first.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    private fun setupGeofence(location: LatLng) {
        lifecycleScope.launch {
            if (sharedViewModel.checkDeviceLocationSettings(requireContext())) {
                drawCircle(location)
                drawMarker(location)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enable device location settings.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun drawMarker(location: LatLng) {
        map.addMarker(
            MarkerOptions().position(location)
                .title(sharedViewModel.geoName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }

    private fun drawCircle(location: LatLng) {
        map.addCircle(
            CircleOptions().center(location).radius(sharedViewModel.geoRadius.toDouble())
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.blue_700))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.blue_transparent))
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onGeofenceReady()
        Toast.makeText(
            requireContext(),
            "Permission Granted! Long Press on the Map to add a Geofence.",
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}