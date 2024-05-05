/**
 * Maintains a location's latitude, longitude, and the video url
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.utility;

public class Location {
    private final double latitude;
    private final double longitude;
    private final String videoURL;

    /**
     * Constructs a new Location object
     * @param lat the latitude of the location
     * @param lng the longitude of the location
     * @param url the url of the location's video
     */
    public Location(double lat, double lng, String url) {
        latitude = lat;
        longitude = lng;
        videoURL = url;
    }

    /**
     * Accessor method to get the location's latitude
     * @return the latitude of the location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Accessor method to get the location's longitude
     * @return the longitude of the location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Accessor method to get the location's video url
     * @return the video url of the location
     */
    public String getVideoURL() {
        return videoURL;
    }

    /**
     * Creates a string representation of the location
     * @return a string with all the location information
     */
    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", videoURL='" + videoURL + '\'' +
                '}';
    }
}
