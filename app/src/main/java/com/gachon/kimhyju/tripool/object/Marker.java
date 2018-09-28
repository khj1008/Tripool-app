package com.gachon.kimhyju.tripool.object;

public class Marker {
    int marker_id;
    String marker_name;
    double latitude;
    double longitude;
    String marker_content;
    int user_id;
    String user_nickName;
    String trip_id;

    public Marker(String marker_name, double latitude, double longitude, String marker_content, int user_id, String user_nickName, String trip_id) {
        this.marker_name = marker_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.marker_content = marker_content;
        this.user_id = user_id;
        this.user_nickName = user_nickName;
        this.trip_id = trip_id;
    }

    public int getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(int marker_id) {
        this.marker_id = marker_id;
    }

    public String getMarker_name() {
        return marker_name;
    }

    public void setMarker_name(String marker_name) {
        this.marker_name = marker_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMarker_content() {
        return marker_content;
    }

    public void setMarker_content(String marker_content) {
        this.marker_content = marker_content;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickName() {
        return user_nickName;
    }

    public void setUser_nickName(String user_nickName) {
        this.user_nickName = user_nickName;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }
}
