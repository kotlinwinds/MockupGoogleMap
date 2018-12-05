package co.winds.add_save

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import co.winds.common.PermissionUtils
import co.winds.common.WorkaroundMapFragment
import co.winds.nearByPartners.MainActivity
import co.winds.restservices.ApiUtils
import co.winds.utils.getAddress
import co.winds.utils.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_partner.*
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.HashMap
import co.winds.R

class AddNearBy : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {


    private val apiServices by lazy { ApiUtils.apiService }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 13
        private val REQUEST_LOCATION = 663
        private var currentAddress:String=""
    }


    lateinit var mMap: GoogleMap
    lateinit var mLastLocation: Location
    var mCurrLocationMarker: Marker? = null
    private var mPermissionDenied = false
    private val mFusedLocationClient by lazy {  LocationServices.getFusedLocationProviderClient(this) }
    private val mLocationRequest by lazy { LocationRequest() }
    private lateinit var mChangeLatLong: LatLng
    private lateinit var mCurrentLatLong: LatLng
    private var mContext: AppCompatActivity?=null




    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                mLastLocation = location

                //Place current location marker
                mChangeLatLong = LatLng(location.latitude, location.longitude)
                try {
                    mCurrentLatLong= LatLng(location.latitude, location.longitude)
                } catch (e: Exception) {
                }
                showMap(mChangeLatLong)

                try {
                    currentAddress=mContext!!.getAddress(mChangeLatLong)
                    edt_address.setText(currentAddress)
                }catch (e:Exception){}
                // viewModel.getNearByVendorList(getUpdateParams(location.latitude, location.longitude))
                // initJson(latLng)
            }
        }
    }


    private fun showMap(latLng: LatLng){
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        // markerOptions.icon((UtilsMapLoading.bitmapDescriptorFromVector(this@AddPartnersAcitivity, R.drawable.ic_pin_pickup)))
        mCurrLocationMarker = mMap.addMarker(markerOptions)
        //move map camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    private var selectValueCategory = ""
    private var list = ArrayList<String>()
    private var categoryId_selected = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(co.winds.R.layout.activity_add_partner)
        mContext=this
        initBroadCastMap()
        viewInit()
        list = ArrayList()
    }


/*Google Map*/

    private fun initBroadCastMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment
        mapFragment.getMapAsync(this)
        (supportFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment)
                .setListener { sv_container!!.requestDisallowInterceptTouchEvent(true) }
    }

    override fun onPause() {
        super.onPause()
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mLocationRequest.interval = 120000 // two minute interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        checkLocationServiceAndProceed(mLocationRequest)


        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true;
        } else {
            // Show rationale and request permission.
        }
        //    imageMarker!!.setImageResource(R.drawable.ic_pin_pickup)
        mMap.setOnCameraIdleListener {
            currentAddress = this.onCameraPositionChangedPickup(mMap.cameraPosition)!!
            edt_address.setText(currentAddress)
            var distance: Double?=null
            try {
                // distance = distance(mCurrentLatLong.latitude,mCurrentLatLong.longitude,mChangeLatLong.latitude,mChangeLatLong.longitude)
                distance = distanceBetween(mCurrentLatLong,mChangeLatLong)
            } catch (e: Exception) {
            }
            var i2: Double?=null
            try {
                val dtime = DecimalFormat("#.##")
                i2 = java.lang.Double.valueOf(dtime.format(distance))
            } catch (e: Exception) {
            }
            this.toast("KM $i2")
            //  val value = parseDouble(DecimalFormat("##.####").format(distance))
            //    toast("KM $value")
        }
        // UtilsMapLoading.setVendorMarker(mMap, this, 23.0284, 72.5068345, "karun", "Bang");
    }



    private fun onCameraPositionChangedPickup(position: CameraPosition): String? {
        mChangeLatLong = position.target
        mMap.clear()
        try {
            return    try {
                this.getAddress(mChangeLatLong)
            } catch (e:Exception){}.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
                mMap.isMyLocationEnabled = true

            } else {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this,LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true)
            }
        } else {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mMap.isMyLocationEnabled = true
        }
    }
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(supportFragmentManager, "dialog")
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val states = LocationSettingsStates.fromIntent(data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK ->
                    enableMyLocation()
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(applicationContext, "permission_rationale_location", Toast.LENGTH_LONG).show()
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
                    e.startResolutionForResult(mContext, REQUEST_LOCATION)
                } catch (sendEx:IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }

            }
        }


    }


    @SuppressLint("ObsoleteSdkInt", "PrivateResource")
    private fun viewInit(){
        var select_name=""
        val items = arrayOf("--Select Category--","Restaurants & Eateries","Home Furnishing","Hotels","Grocery/ Kirana Stores","Electronic Appliances","Cosmetics Shops","Cosmetics Shops",
            "Foot wear","Sports Wear","Toy Shop","Bakery","Meat Products","Book, Stationary Shop","Agro Products","Pumps & Hardware","Computer Products",
            "Computer Products","Mobile shops","Fabricators","Hospitals","Kids Clothing","Bag, Belts, Wallet","Men’s Clothing",
            "Men’s Clothing","Women Clothing","Furniture","Bicycle","Sweet Shops","Tailors")
        val adp= ArrayAdapter<String>(this, R.layout.select_dialog_item_material, items)
        category_partner.adapter=adp

        category_partner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                select_name=parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO Auto-generated method stub
            }

        }


        btn_save.setOnClickListener {

            val establish_name = edt_establish_name.text.toString()
            val owner_name = edt_owner_name.text.toString()


            if (select_name == "--Select Category--") {
                Toast.makeText(applicationContext, "Please Select Category", Toast.LENGTH_SHORT).show()
            } else if (establish_name.isBlank()) {
                Toast.makeText(applicationContext, "Please enter Establish Name", Toast.LENGTH_SHORT).show()
            } else if (owner_name.isBlank()) {
                Toast.makeText(applicationContext, "Please enter owner Name", Toast.LENGTH_SHORT).show()
            } else {
                //  Log.d("Tags", "$select_name - $establish_name - $owner_name - ${mChangeLatLong.latitude } - ${mChangeLatLong.longitude } ")
                //  Log.d("Tags", "$currentAddress ")

                val apiParams = HashMap<String, String>()
                apiParams["category"] = select_name
                apiParams["business_name"] = establish_name
                apiParams["ownerName"] = owner_name
                apiParams["latitude"] = mChangeLatLong.latitude.toString()
                apiParams["longitude"] =  mChangeLatLong.longitude.toString()

                apiServices.getRegister(apiParams)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                                result-> val data=result
                            Toast.makeText(applicationContext,data.message, Toast.LENGTH_SHORT).show()
                            //  val datas= Gson().toJson(data)
                            //  Log.d("TAHS","Response JSON = $datas")
                            // recycler_view.adapter=MyAdapter(data)

                            edt_establish_name.text!!.clear()
                            edt_owner_name.text!!.clear()
                        },
                        { error ->
                            Log.d("TAHS","ERROR ${error.message}")
                        }
                    )

            }
        }

        //  nearBy()
    }



    fun btn_view(v: View){
        startActivity(Intent(this, MainActivity::class.java))
    }


    private fun distanceBetween(point1: LatLng?, point2: LatLng?): Double? {
        return if (point1 == null || point2 == null) {
            null
        } else  SphericalUtil.computeDistanceBetween(point1, point2)/1000

    }

}