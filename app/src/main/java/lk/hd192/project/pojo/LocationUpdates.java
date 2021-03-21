package lk.hd192.project.pojo;

public class LocationUpdates {
    private Double Latitude, Longitude;
    String Status;

    public LocationUpdates() {
    }

    public LocationUpdates(Double latitude, Double longitude, String status) {

        Latitude = latitude;
        Longitude = longitude;
        Status = status;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
