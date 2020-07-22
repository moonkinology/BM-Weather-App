package com.example.bmweather

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext

class Location () {

    val TAG = "PermissionDemo"

    /*  these two have to be declare/initialised @Top */
    var permissionsList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val permissionsList_request_Code = 10

    fun showToast(mContext: Context?, message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun makeMultipleRequest(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsList,
            permissionsList_request_Code
        )
    }





    fun setupPermissions(context: Context, activity: MainActivity) {
        val FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        /*
         Only if on the first start, the user clicks on deny, the next
         time he starts the app an intro Dialog will be shown to explain the reason
        for asking permissions, then when he clicks on "OK",
        once again he will be asked for permission.

         */
        if (FINE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED || COARSE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Permission to access the Location is required for this app to Show results based on your LAST KNOWN LOCATION.")
                    .setTitle("Permission required")
                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    Log.i(TAG, "Clicked")
                    makeMultipleRequest(activity)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                makeMultipleRequest(activity)
            }
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun setUpLocationListener(latTextView: TextView,lngTextView: TextView,activity: MainActivity, context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // if the permissions were not granted , ask for permissions
            makeMultipleRequest(activity)
            return

        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        latTextView.text = location.latitude.toString()
                        lngTextView.text = location.longitude.toString()
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }




 // It should be overridden but how ?



    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray, context: Context
    ) {
        when (requestCode) {
            permissionsList_request_Code -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                    Toast.makeText(
                        context, "Data Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }

    }



}







