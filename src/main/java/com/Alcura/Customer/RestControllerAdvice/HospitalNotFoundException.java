package com.Alcura.Customer.RestControllerAdvice;

public class HospitalNotFoundException extends RuntimeException {
    public HospitalNotFoundException() {
        super("No hospitals found near the specific location");
    }
}
