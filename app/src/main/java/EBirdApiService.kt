import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EBirdApiService {
    @GET("/v2/data/obs/geo/recent")
    suspend fun getRecentObservations(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("sort") sort: String = "species",
        @Query("dist") distance: Int = 5,
        @Header("X-eBirdApiToken") apiKey: String
    ): List<Observations>
}