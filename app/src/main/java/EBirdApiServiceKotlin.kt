
class EBirdApiServiceKotlin {

    companion object{
        public var dist = 25
        public var long = 18.0
        public var lat = -33.0

        public var listOfUserObservations = mutableListOf<UserObservation>()

        fun addItem(longitude: Double, latitude: Double, name: String) {
            val newItem = UserObservation(longitude, latitude, name)
            listOfUserObservations.add(newItem)
        }
    }

}