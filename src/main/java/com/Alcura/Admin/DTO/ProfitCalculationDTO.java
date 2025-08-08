package com.Alcura.Admin.DTO;

import java.time.LocalDateTime;

public class ProfitCalculationDTO {
    private String unique_id;
    private String doctor;
    private String doctor_name;
    private String customer_email;
    private String status;
    private String paymentId;
    private LocalDateTime created_at;
    private String uniqueId;
    private String email;
    private Double hospital_price;
    private Float newPrice;
    private Float price;
    private String doctor_email;

    // Constructor should match exactly with the query parameters
    public ProfitCalculationDTO(
            String unique_id,
            String doctor,
            String doctor_name,
            String customer_email,
            String status,
            String paymentId,
            LocalDateTime created_at,
            String uniqueId,
            String email,
            Double hospital_price,
            Float newPrice,
            Float price,
            String doctor_email
    ) {
        this.unique_id = unique_id;
        this.doctor = doctor;
        this.doctor_name = doctor_name;
        this.customer_email = customer_email;
        this.status = status;
        this.paymentId = paymentId;
        this.created_at = created_at;
        this.uniqueId = uniqueId;
        this.email = email;
        this.hospital_price = hospital_price;
        this.newPrice = newPrice;
        this.price = price;
        this.doctor_email = doctor_email;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getHospital_price() {
        return hospital_price;
    }

    public void setHospital_price(Double hospital_price) {
        this.hospital_price = hospital_price;
    }

    public Float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Float newPrice) {
        this.newPrice = newPrice;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDoctor_email() {
        return doctor_email;
    }

    public void setDoctor_email(String doctor_email) {
        this.doctor_email = doctor_email;
    }
}