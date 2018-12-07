package co.winds.model

import com.google.gson.annotations.SerializedName


data class ResponseCategoryModel(
        val message: String,
        @SerializedName("data")
        val arrayListModel: List<CategoryModel>
)
data class CategoryModel(
    @SerializedName("commission")
    var commission: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("status")
    var status: String?,
    var isSelected: Boolean
)

