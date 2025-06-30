package com.Alcura.Customer.Model;

import com.Alcura.Doctor.Model.Doctor;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "appointment")
public class Appoinment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "unique_id")
    private String unique_id;

    @JoinColumn(name = "doctor_id")
    private String doctor;

    @Column(name = "doctor_name")
    private String doctor_name;


    @Column(name = "customer_email")
    private String customer_email;

    @Column(name = "appointment_date")
    private LocalDate appointment_date;

    @Column(name = "appointment_time")
    private String appointment_time;

    @Column(name = "special_reasons")
    private String special_reasons;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalTime created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(LocalDate appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getSpecial_reasons() {
        return special_reasons;
    }

    public void setSpecial_reasons(String special_reasons) {
        this.special_reasons = special_reasons;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalTime created_at) {
        this.created_at = created_at;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }
}
