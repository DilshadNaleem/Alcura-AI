package com.Alcura.Customer.RestControllerAdvice;

public class HospitalNotFoundException extends RuntimeException {
    public HospitalNotFoundException(String noNearbyHospitalsFound) {
        super("No hospitals found near the specific location");
    }
}
