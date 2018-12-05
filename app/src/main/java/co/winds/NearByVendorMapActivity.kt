package co.winds

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import co.winds.common.BaseActivity
import co.winds.common.PermissionUtils
import co.winds.model.NearbyVendor
import co.winds.restservices.ApiUtils
import co.winds.utils.getAddress
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*


class NearByVendorMapActivity : AppCompatActivity() , OnMapReadyCallback , ActivityCompat.OnRequestPermissionsResultCallback  {


    private val apiServices by lazy { ApiUtils.apiService }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 1
        val REQUEST_LOCATION = 668
    }

    lateinit var mGoogleMap: GoogleMap
    lateinit var mLastLocation: Location
    var mCurrLocationMarker: Marker? = null
    private var mPermissionDenied = false
    private val mFusedLocationClient by lazy {  LocationServices.getFusedLocationProviderClient(this) }
    private val mLocationRequest by lazy {LocationRequest() }
    private val miter:Int=1000
    private var mCurrentLatLng:LatLng?=null




    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }
                //Place current location marker
                try {
                    nearBy()
                } catch (e: Exception) { }
                mCurrentLatLng = LatLng(location.latitude, location.longitude)
                mGoogleMap.clear()
                mGoogleMap.addMarker(MarkerOptions().position(mCurrentLatLng!!).title(getAddress(mCurrentLatLng!!)))
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLatLng!!))
                if(miter==500) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15.7f), null)
                }else if(miter==1000){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14.7f), null)
                }else if(miter==1500){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14.1f), null)
                }else if(miter==2000){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13.7f), null)
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_vendors_map)
        initGoogleMap()
    }

    private fun initGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    override fun onPause() {
        super.onPause()
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
        mLocationRequest.interval = 120000 // two minute interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        checkLocationServiceAndProceed(mLocationRequest)
        mGoogleMap.uiSettings.isZoomControlsEnabled = false
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.isMyLocationEnabled = true;
        } else {
            // Show rationale and request permission.
        }


        mGoogleMap.setOnMapLoadedCallback {
            // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30))
            // run()
            try {

                mGoogleMap.addMarker(MarkerOptions().position(mCurrentLatLng!!).title("Marker in Sydney"))
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLatLng!!))
                if(miter==500) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15.7f), null)
                }else if(miter==1000){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14.7f), null)
                }else if(miter==1500){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14.1f), null)
                }else if(miter==2000){
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13.7f), null)
                }
                drawMarkerWithCircle(mCurrentLatLng!!, miter.toDouble())
            } catch (e: Exception) {
            }
        }
    }




    override fun onResumeFragments() {
        super.onResumeFragments()
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            mPermissionDenied = false
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            checkLocationServiceAndProceed(mLocationRequest)
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true
        }
    }


    private fun enableMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mGoogleMap.isMyLocationEnabled = false

            } else {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true)
            }
        } else {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mGoogleMap.isMyLocationEnabled = false
        }
    }
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(supportFragmentManager, "dialog")
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val states = LocationSettingsStates.fromIntent(data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK ->
                    enableMyLocation()
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(applicationContext, R.string.permission_rationale_location, Toast.LENGTH_LONG).show()
                    this.finish()
                }
                else -> {
                }
            }
        }
    }
    private fun checkLocationServiceAndProceed(mLocationRequest: LocationRequest) {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) { enableMyLocation() }
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@NearByVendorMapActivity, REQUEST_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }

            }
        }

    }



    @SuppressLint("CheckResult")
    private fun nearBy() {
        apiServices.getNearby()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    val data = result.data!!.nearbyVendor
                    Toast.makeText(applicationContext, "${result.message}", Toast.LENGTH_SHORT).show()
                    setMap(data)
                    Log.d("Tags", " $data")

                },
                { error ->
                    Log.d("TAHS", "ERROR ${error.message}")
                }
            )

    }
    private fun setMap(data: List<NearbyVendor?>?) {
        mGoogleMap.clear()

        val bulder = LatLngBounds.Builder()
        // bulder.include()
        // bulder.include()

        for (l in data!!) {
            val latLng = LatLng(l!!.latitude!!, l.longitude!!)
            bulder.include(latLng)
            val markarOption = MarkerOptions().position(latLng).title(l.category).snippet(l.businessName)
            mGoogleMap.addMarker(markarOption)
        }

        val bound = bulder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bound, 100)
        mGoogleMap.moveCamera(cameraUpdate)
    }
    private fun drawMarkerWithCircle(position: LatLng,radiusInMeters:Double) {
        // radiusInMeters  // increase decrease this distancce as per your requirements

        val circleOptions = CircleOptions()
            .center(position)
            .radius(radiusInMeters)
            .fillColor(Color.parseColor("#35303F9F"))
            .strokeColor(Color.TRANSPARENT)
            .strokeWidth(2f)
             mGoogleMap.addCircle(circleOptions)

         val markerOptions = MarkerOptions().position(position)
         mGoogleMap.addMarker(markerOptions)
    }
}
