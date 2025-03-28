package com.example.geofancingapplication.util

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.geofancingapplication.R
import com.example.geofancingapplication.ui.addgeofence.viewmodels.Step1ViewModel
import com.example.geofancingapplication.viewmodels.SharedViewModel
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("updateGeofenceName", "enableNextButton", requireAll = true)
fun TextInputEditText.onTextChanged(
    sharedViewModel: SharedViewModel,
    step1ViewModel: Step1ViewModel
) {
    this.setText(sharedViewModel.geoName)
    Log.d("Step1Fragment", "onTextChanged: ${sharedViewModel.geoName}")
    this.doOnTextChanged { text, _, _, _ ->
        if (text.isNullOrEmpty()) {
            step1ViewModel.enabledNextButton(false)
        } else {
            step1ViewModel.enabledNextButton(true)
        }
        sharedViewModel.geoName = text.toString()
        Log.d("Step1Fragment", "onTextChanged: ${sharedViewModel.geoName}")
    }
}

@BindingAdapter("nextButtonEnabled", "saveGeofenceID", requireAll = true)
fun TextView.step1NextClicked(nextButtonEnabled: Boolean, sharedViewModel: SharedViewModel) {
    this.setOnClickListener {
        if (nextButtonEnabled) {
            sharedViewModel.geoID = System.currentTimeMillis()
            this.findNavController().navigate(R.id.action_step1Fragment_to_step2Fragment)
        }
    }
}

@BindingAdapter("setProgressVisibility")
fun ProgressBar.setProgressVisibility(nextButtonEnabled: Boolean) {
    if (nextButtonEnabled) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}