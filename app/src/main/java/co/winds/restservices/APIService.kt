package co.winds.restservices
import co.winds.model.ResponseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface APIService {


    @GET("/vendor/register")
    fun getCustomerAll(): Observable<ResponseModel>


    @Multipart
    @POST("")
    fun saveRegisterShop(
                    @Part("categoryId")  categoryId: RequestBody,
                    @Part("shopName") shopName: RequestBody,
                    @Part("shopReg") shopReg: RequestBody,
                    @Part ownerAvatar: MultipartBody.Part,
                    @Part shopAvatar: MultipartBody.Part): Observable<ResponseModel>



    @GET("/vendor/nearby")
    fun getNearby(): Observable<ResponseModel>


    @POST("/vendor/register")
    @FormUrlEncoded
    fun getRegister(@FieldMap param : Map<String,String>): Observable<ResponseModel>

/*

    @Multipart
    @POST("/immigration/api/updateProfile")
    fun postImage(
                  @Part image: MultipartBody.Part,
                  @Part("firstName") firstName: String,
                  @Part("lastName") lastName: String,
                  @Part("contact") contact: String,
                  @Part("countryCode") countryCode: String
    ): Call<ResponseModel>
*/





    /*@Headers("Content-Type: application/json")
    @POST("/immigration/api/verifyOtp")
    fun verifyOtp(@Body body: Map<String, String>): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("/immigration/api/resendOtp")
    fun resendOtp(@Body body: Map<String, String>): Call<ResponseModel>


    @Multipart
    @POST("/immigration/api/updateProfile")
    fun postImage(@Header ("accessToken") accessToken:String,
                  @Part image: MultipartBody.Part,
                  @Part("firstName") firstName: String,
                  @Part("lastName") lastName: String,
                  @Part("contact") contact: String,
                  @Part("countryCode") countryCode: String
                 ): Call<ResponseModel>


*/


/*

    @get:GET("/avatar_1.json")
    val avatar1: Call<JSONObject>

    @FormUrlEncoded
    @POST("/")
    fun Save(@Field("answer") name:String,
             @Field("Date") Date:String):Call<JSONObject>
*/


}