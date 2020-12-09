package lk.hd192.project.pojo;

public class LocationUpdates {

    private Double Latitude,Longitude;

    public LocationUpdates() {
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public LocationUpdates(Double latitude, Double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
