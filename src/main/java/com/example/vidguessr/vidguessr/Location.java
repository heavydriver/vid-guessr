package com.example.vidguessr.vidguessr;

public class Location {
    private final double latitude;
    private final double longitude;
    private final String videoURL;

    public Location(double lat, double lng, String url) {
        latitude = lat;
        longitude = lng;
        videoURL = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getVideoURL() {
        return videoURL;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", videoURL='" + videoURL + '\'' +
                '}';
    }
}
