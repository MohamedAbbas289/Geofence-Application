package com.example.geofancingapplication.ui.addgeofence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geofancingapplication.R
import com.example.geofancingapplication.databinding.FragmentStep3Binding
import com.example.geofancingapplication.viewmodels.SharedViewModel

class Step3Fragment : Fragment() {
    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep3Binding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.step3Back.setOnClickListener {
            findNavController()
                .navigate(R.id.action_step3Fragment_to_step2Fragment)
        }

        binding.step3Done.setOnClickListener {
            sharedViewModel.geoRadius = binding.slider.value
            sharedViewModel.geofenceReady = true
            findNavController()
                .navigate(R.id.action_step3Fragment_to_mapsFragment)
            Log.d("Step3Fragment", "geoRadius: ${sharedViewModel.geoRadius}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}