package  co.winds.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("error")
    val error: List<Any?>?,
    @SerializedName("message")
    val message: String?
)

data class Data(
    @SerializedName("nearby_vendor")
    val nearbyVendor: List<NearbyVendor?>?
)

data class NearbyVendor(
    @SerializedName("business_name")
    val businessName: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("ownerName")
    val ownerName: String?,
    @SerializedName("address")
    val address: String,
    @SerializedName("vender_distance")
    val vender_distance: String?,
    @SerializedName("userId")
    val userId: Int?,
    var isSelected: Boolean
)

