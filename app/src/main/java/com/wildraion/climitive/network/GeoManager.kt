package com.wildraion.climitive.network

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.wildraion.climitive.R
import javax.inject.Inject


class GeoManager @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val cancellationTokenSource: CancellationTokenSource
) {

    var geoCallbackListener: GeoCallbackListener? = null

    fun fetchLocation(context: ComponentActivity) {
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            getRequestPermissionLauncher(context)
                .launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }



        val task = fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
        task.addOnSuccessListener {
            geoCallbackListener?.onSuccessListener(it)
        }
    }

    private fun getRequestPermissionLauncher(context: ComponentActivity): ActivityResultLauncher<String> {
        return context.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                fetchLocation(context)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_message),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getLocationTaskCancellation() {
        cancellationTokenSource.cancel()
    }

    interface GeoCallbackListener {
        fun onSuccessListener(location: Location?)
    }
}