import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface EBirdApiService {
    @GET("/v2/data/obs/geo/recent")
    suspend fun getRecentObservations(

        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("sort") sort: String = "species",
        @Query("dist") distance: Int = EBirdApiServiceKotlin.dist,
        @Query("Back") back: Int = 5,
        @Header("X-eBirdApiToken") apiKey: String
    ): List<Observations>

    @GET
    suspend fun getHotspotDetailsCsv(@Url url: String): Response<ResponseBody>
}