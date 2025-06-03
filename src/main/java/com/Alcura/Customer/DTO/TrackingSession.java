package com.Alcura.Customer.DTO;


public class TrackingSession
{
    private String id;
    private  Long hospitalId;
    private double latitude;
    private double longitude;


    public TrackingSession (String id, Long hospitalId, double latitude, double longitude)
    {
        this.id = id;
        this.hospitalId = hospitalId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateLocation (double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
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
}
