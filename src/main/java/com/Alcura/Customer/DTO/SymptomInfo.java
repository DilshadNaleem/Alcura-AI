package com.Alcura.Customer.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SymptomInfo {

    @JsonProperty("disease")
    private String disease;

    @JsonProperty("confidence")
    private int confidence;

    @JsonProperty("info")
    private Map<String, String> info;  // Holds keys like Description, Symptoms, etc.

    @JsonProperty("images")
    private List<String> images;  // List of base64-encoded images

    // Getters and Setters

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
