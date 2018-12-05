package co.winds.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

/**
 * Created by Manish Kumar on 12/2/2018
 */

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Context.log(message: String) {
    Log.d("TAGS",message)
}

fun Context.getAddress(location: LatLng): String {
    val geocoder = Geocoder(this, Locale.getDefault())
    var addresstxt = ""
    try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (null != addresses && !addresses.isEmpty()) {
            addresstxt = "" + addresses[0].getAddressLine(0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        addresstxt=""
    }
    return addresstxt
}