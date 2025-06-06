package com.Alcura.Customer.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiseaseInfo
{
    @JsonProperty("Disease")
    private String disease;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("symptoms")
    private String symptoms;

    @JsonProperty("cause")
    private String cause;

    @JsonProperty("side_effects")
    private String sideeffects;

    @JsonProperty("treatment")
    private String treatment;

    @JsonProperty("medications")
    private String medications;

    @JsonProperty("prevention")
    private String prevention;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("risk_factors")
    private String riskFactors;

    @JsonProperty("is_contagious")
    private String contagious;

    @JsonProperty("common_age_group")
    private String commonAgeGroup;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("first_aid_advice")
    private String firstAidAdvice;

    @JsonProperty("Source_of_information")
    private String sourceOfInformation;

    @JsonProperty("scientific_name")
    private String scientificName;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public DiseaseInfo(){}

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSideeffects() {
        return sideeffects;
    }

    public void setSideeffects(String sideeffects) {
        this.sideeffects = sideeffects;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getPrevention() {
        return prevention;
    }

    public void setPrevention(String prevention) {
        this.prevention = prevention;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    public String getContagious() {
        return contagious;
    }

    public void setContagious(String contagious) {
        this.contagious = contagious;
    }

    public String getCommonAgeGroup() {
        return commonAgeGroup;
    }

    public void setCommonAgeGroup(String commonAgeGroup) {
        this.commonAgeGroup = commonAgeGroup;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFirstAidAdvice() {
        return firstAidAdvice;
    }

    public void setFirstAidAdvice(String firstAidAdvice) {
        this.firstAidAdvice = firstAidAdvice;
    }

    public String getSourceOfInformation() {
        return sourceOfInformation;
    }

    public void setSourceOfInformation(String sourceOfInformation) {
        this.sourceOfInformation = sourceOfInformation;
    }
}
