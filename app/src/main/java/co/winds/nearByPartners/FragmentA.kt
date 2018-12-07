package co.winds.nearByPartners


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import co.winds.R
import co.winds.common.PermissionUtils
import co.winds.model.NearbyVendor
import co.winds.nearByPartners.adapter.FilterActivityDialog
import co.winds.restservices.ApiUtils
import co.winds.utils.getAddress
import co.winds.utils.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentA : Fragment(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,IFilterDataCallback{


    private  var mContext:Context?=null
   private lateinit var mActivity:AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity=(activity!! as AppCompatActivity)
        mContext=(activity!! as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun isFilter(filterData: String) {
        mContext!!.toast(filterData)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_mapview, container, false)
        initGoogleMap()
        return view
    }


    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                startActivityForResult(Intent(mActivity, FilterActivityDialog::class.java),3324)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private val apiServices by lazy { ApiUtils.apiService }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 1
        val REQUEST_LOCATION = 668
    }

    lateinit var mGoogleMap: GoogleMap
    lateinit var mLastLocation: Location
    var mCurrLocationMarker: Marker? = null
    private var mPermissionDenied = false
    private val mFusedLocationClient by lazy {  LocationServices.getFusedLocationProviderClient(mContext!!) }
    private val mLocationRequest by lazy { LocationRequest() }
    private val miter:Int=1000
    private var mCurrentLatLng: LatLng?=null




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
                    nearBy("")
                } catch (e:Exception) { }
                mCurrentLatLng = LatLng(location.latitude, location.longitude)
                mGoogleMap.clear()
                mGoogleMap.addMarker(MarkerOptions().position(mCurrentLatLng!!).title(mContext!!.getAddress(mCurrentLatLng!!)))
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

    override fun onPause() {
        super.onPause()
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

        if (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION)
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


    override fun onResume() {
        super.onResume()
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
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mGoogleMap.isMyLocationEnabled = false

            } else {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(mActivity, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true)
            }
        } else {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mGoogleMap.isMyLocationEnabled = false
        }
    }
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(mActivity.supportFragmentManager, "dialog")
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                RESULT_OK ->
                    enableMyLocation()
                RESULT_CANCELED -> {
                    Toast.makeText(mActivity, R.string.permission_rationale_location, Toast.LENGTH_LONG).show()
                    mActivity.finish()
                }
                else -> {
                }
            }
            3324->when(resultCode) {
                RESULT_OK -> {
                    val returnString = data!!.getStringExtra("keyName")
                    mActivity.toast(returnString)
                    nearBy(returnString)
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }
    private fun checkLocationServiceAndProceed(mLocationRequest: LocationRequest) {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(mActivity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(mActivity) { enableMyLocation() }
        task.addOnFailureListener(mActivity) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(mActivity, REQUEST_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }

            }
        }

    }


    @SuppressLint("CheckResult")
    private fun nearBy(str:String) {
        apiServices.getNearby(str)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    val data = result.data!!.nearbyVendor
                   // Toast.makeText(mContext, "${result.message}", Toast.LENGTH_SHORT).show()
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
    private fun drawMarkerWithCircle(position: LatLng, radiusInMeters:Double) {
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
