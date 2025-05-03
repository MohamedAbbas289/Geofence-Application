package com.example.geofancingapplication.ui.geofences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofancingapplication.databinding.FragmentGeofenceBinding
import com.example.geofancingapplication.ui.adapters.GeofencesAdapter
import com.example.geofancingapplication.viewmodels.SharedViewModel


class GeofenceFragment : Fragment() {
    private var _binding: FragmentGeofenceBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val geofenceAdapter by lazy { GeofencesAdapter(sharedViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeofenceBinding.inflate(inflater, container, false)
        observeDatabase()
        setupToolbar()
        return binding.root
    }

    private fun observeDatabase() {
        sharedViewModel.readGeofences.observe(viewLifecycleOwner) { geofences ->
            geofenceAdapter.setData(geofences)
            binding.geofencesRecyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this
        binding.geofencesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.geofencesRecyclerView.adapter = geofenceAdapter

    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher
                .onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}