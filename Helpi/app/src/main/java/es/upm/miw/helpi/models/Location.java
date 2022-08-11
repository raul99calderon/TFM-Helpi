package es.upm.miw.helpi.models;

public class Location {
    private String place;
    private Double latitude, longitude;

    public Location() {
    }

    public Location(String place, Double latitude, Double longitude) {
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
