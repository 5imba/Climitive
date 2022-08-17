package com.wildraion.climitive.network

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.ComponentActivity
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

    fun fetchLocation(
        activity: ComponentActivity,
        onSuccessCallback: (location: Location?) -> Unit
    ) {

        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If not granted start permission launcher
            val requestPermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    fetchLocation(activity, onSuccessCallback)
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.location_permission_message),
                        Toast.LENGTH_LONG).show()
                }
            }
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        } else {
            // Get location
            val task = fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            task.addOnSuccessListener { onSuccessCallback(it) }
        }
    }

    fun getLocationTaskCancellation() {
        cancellationTokenSource.cancel()
    }
}