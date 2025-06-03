package com.Alcura.Customer.Model;

import jakarta.persistence.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Entity
@Table(name = "hospitals")
public class Hospital
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal latitude;
    private String name;
    private BigDecimal longitude;
    private String contactEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
