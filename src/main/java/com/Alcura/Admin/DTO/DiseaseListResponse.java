package com.Alcura.Admin.DTO;

import java.util.List;

public class DiseaseListResponse {
    private List<String> diseases;

    // Add default constructor
    public DiseaseListResponse() {
    }

    public DiseaseListResponse(List<String> diseases) {
        this.diseases = diseases;
    }

    // Ensure proper getter naming (must match JSON property)
    public List<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<String> diseases) {
        this.diseases = diseases;
    }
}