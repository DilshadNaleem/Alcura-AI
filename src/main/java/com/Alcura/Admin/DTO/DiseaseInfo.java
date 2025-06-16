package com.Alcura.Admin.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiseaseInfo {
    @JsonProperty("Description")
    private String description;

    @JsonProperty("Disease")
    private String disease;

    @JsonProperty("Source_of_information")
    private String sourceOfInformation;

    @JsonProperty("cause")
    private String cause;

    @JsonProperty("common_age_group")
    private String commonAgeGroup;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("first_aid_advice")
    private String firstAidAdvice;

    @JsonProperty("is_contagious")
    private String contagious; // Changed to String to match your JSON ("No"/"Yes")

    @JsonProperty("medications")
    private String medications;

    @JsonProperty("prevention")
    private String prevention;

    @JsonProperty("risk_factors")
    private String riskFactors;

    @JsonProperty("scientific_name")
    @JsonSetter(nulls = Nulls.SKIP)
    private String scientificName = "N/A"; // Handles NaN as null

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("side_effects")
    private String sideEffects;

    @JsonProperty("symptoms")
    private String symptoms;

    @JsonProperty("treatment")
    private String treatment;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getSourceOfInformation() {
        return sourceOfInformation;
    }

    public void setSourceOfInformation(String sourceOfInformation) {
        this.sourceOfInformation = sourceOfInformation;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
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

    public String getContagious() {
        return contagious;
    }

    public void setContagious(String contagious) {
        this.contagious = contagious;
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

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
}