public class Location {
    private final String cityName;
    private final double latitude;
    private final double longitude;

    public Location(String cityName, double latitude, double longitude) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(Location other) {
        final double earthRadiusKm = 6371.0;
        double latitudeDistance = Math.toRadians(other.latitude - latitude);
        double longitudeDistance = Math.toRadians(other.longitude - longitude);
        double startLatitude = Math.toRadians(latitude);
        double endLatitude = Math.toRadians(other.latitude);

        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2)
                + Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2)
                * Math.cos(startLatitude) * Math.cos(endLatitude);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    @Override
    public String toString() {
        return cityName;
    }
}
