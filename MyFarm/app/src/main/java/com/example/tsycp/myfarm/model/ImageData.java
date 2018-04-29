package com.example.tsycp.myfarm.model;

public class ImageData {

    private String imagename;
    private String latitude;
    private String longitude;
    private String zone;
    private String timestamp;

    public ImageData() {
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.latitude = imagename;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
