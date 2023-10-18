import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {
    companion object {
        private const val BASE_URL = "https://api.ebird.org"

        fun create(): EBirdApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(EBirdApiService::class.java)
        }
    }
}