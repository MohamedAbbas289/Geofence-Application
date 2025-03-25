package com.example.geofancingapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if(Permissions.hasLocationPermission(this)){
//            findNavController(R.id.navHostFragment).navigate(R.id.action_permissionFragment_to_mapsFragment)
//        }
    }
}