package com.Alcura.Doctor.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "unique_id", unique = true)
    private String uniqueId;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nic", nullable = false)
    private String nic;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "face_data")
    private String faceData;

    @Column(name = "user_type", nullable = false)
    private String DoctorType;

    @Column(name = "specialist")
    private String specialist;

    @Column(name = "special_info")
    private String specialist_info;

    @Column(name = "experience")
    private String experience;

    @Column(name = "other_specialization")
    private String other_specialization;

    @Column(name = "special_note")
    private String special_note;

    @Column(name = "qualification")
    private String Qualification;

    @Column( name = "government_hospital")
    private String government_Hospitals;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    @Column(name = "doctor_availability", nullable = false)
    private LocalTime doctor_availablility;

    // Getters and Setters


    public LocalTime getDoctor_availablility() {
        return doctor_availablility;
    }

    public void setDoctor_availablility(LocalTime doctor_availablility) {
        this.doctor_availablility = doctor_availablility;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public boolean isFirstLogin(boolean b) {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getSpecialist_info() {
        return specialist_info;
    }

    public void setSpecialist_info(String specialist_info) {
        this.specialist_info = specialist_info;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getOther_specialization() {
        return other_specialization;
    }

    public void setOther_specialization(String other_specialization) {
        this.other_specialization = other_specialization;
    }

    public String getSpecial_note() {
        return special_note;
    }

    public void setSpecial_note(String special_note) {
        this.special_note = special_note;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getGovernment_Hospitals() {
        return government_Hospitals;
    }

    public void setGovernment_Hospitals(String government_Hospitals) {
        this.government_Hospitals = government_Hospitals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFaceData() {
        return faceData;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }

    public String getDoctorType() {
        return DoctorType;
    }

    public void setDoctorType(String doctorType) {
        DoctorType = doctorType;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }
}