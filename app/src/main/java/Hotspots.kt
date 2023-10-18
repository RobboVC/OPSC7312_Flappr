import com.google.gson.annotations.SerializedName

data class Hotspots(
    @SerializedName("locID") val locID: String,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("subnational1Code") val subnational1Code: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("name") val name: String,
    @SerializedName("creationDt") val creationDt: String,
    @SerializedName("obsCount") val obsCount: Int
)
