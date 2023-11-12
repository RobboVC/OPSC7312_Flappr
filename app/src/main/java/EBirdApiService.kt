
import com.example.opsc7312_flappr.NotableObservations
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface EBirdApiService {
    @GET("/v2/data/obs/geo/recent")
    suspend fun getRecentObservations(

        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("sort") sort: String = "species",
        @Query("dist") distance: Int = EBirdApiServiceKotlin.dist,
        @Query("back") back: Int = 14,
        @Header("X-eBirdApiToken") apiKey: String
    ): List<Observations>

    @GET
    suspend fun getHotspotDetailsCsv(@Url url: String): Response<ResponseBody>

    //get Recent nearby notable observations
    @GET("/v2/data/obs/geo/recent/notable")
    suspend fun getNotableBirds(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("back") back: Int = 7,
        @Query("dist") distance: Int = EBirdApiServiceKotlin.dist,
        @Header("X-eBirdApiToken") apiKey: String
    ): List<NotableObservations>

    @GET
    suspend fun getNotableDetailsCsv(@Url url: String): Response<ResponseBody>


}

