package com.example.geofancingapplication.util

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.geofancingapplication.R
import com.example.geofancingapplication.ui.addgeofence.viewmodels.Step1ViewModel
import com.example.geofancingapplication.viewmodels.SharedViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

@BindingAdapter("setCity")
fun TextView.setCity(prediction: AutocompletePrediction) {
    this.text = prediction.getPrimaryText(null).toString()
}

@BindingAdapter("setCountry")
fun TextView.setCountry(prediction: AutocompletePrediction) {
    this.text = prediction.getSecondaryText(null).toString()
}

@BindingAdapter("handleNetworkConnection", "handleRecyclerView", requireAll = true)
fun TextInputLayout.handleNetworkConnection(networkAvailable: Boolean, recyclerView: RecyclerView) {
    if (!networkAvailable) {
        this.isErrorEnabled = true
        this.error = "No Internet Connection."
        recyclerView.hide()
    } else {
        this.isErrorEnabled = false
        this.error = null
        recyclerView.show()
    }
}

//step3 fragment
@BindingAdapter("updateSliderValueTextView", "getGeoRadius", requireAll = true)
fun Slider.updateSliderValue(textView: TextView, sharedViewModel: SharedViewModel) {
    updateSliderValueTextView(sharedViewModel.geoRadius, textView)
    this.addOnChangeListener { _, value, _ ->
        sharedViewModel.geoRadius = value
        updateSliderValueTextView(sharedViewModel.geoRadius, textView)
    }
}

fun Slider.updateSliderValueTextView(geoRadius: Float, textView: TextView) {
    val kilometers = geoRadius / 1000
    if (geoRadius >= 1000f) {
        textView.text = context.getString(R.string.display_kilometers, kilometers.toString())
    } else {
        textView.text = context.getString(R.string.display_meters, geoRadius.toString())
    }
    this.value = geoRadius
}