package co.winds.restservices


object ApiUtils {

    val BASE_URL="http://192.168.43.18:3000"
    val apiService: APIService
        get() = RetrofitClient.getClient(BASE_URL).create(APIService::class.java)
}