package com.Alcura.Customer.DTO;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_requests")
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private Long hospitalId;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private LocalDateTime requestTime;

    private BigDecimal hospitalLatitude;  // Add this field
    private BigDecimal hospitalLongitude; // And this if needed

    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, CANCELLED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public BigDecimal getHospitalLatitude() {
        return hospitalLatitude;
    }

    public void setHospitalLatitude(BigDecimal hospitalLatitude) {
        this.hospitalLatitude = hospitalLatitude;
    }

    public BigDecimal getHospitalLongitude() {
        return hospitalLongitude;
    }

    public void setHospitalLongitude(BigDecimal hospitalLongitude) {
        this.hospitalLongitude = hospitalLongitude;
    }
}
