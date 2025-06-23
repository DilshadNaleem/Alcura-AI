package com.Alcura.Admin.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicineInfo {
    // 'class_name': '', 'dosage': '', 'use': '', 'price': '', 'side_effects': '', 'dosage_form': '',
    //            'Scientific_Name': '', 'max_dose': '', 'administration': '', 'indications': '', 'precautions': '',
    //            'serious_effects': '', 'contraindications': '', 'Source_of_information': ''

    @JsonProperty("class_name")
    private String ClassName;

    @JsonProperty("dosage")
    private String Dosage;

    @JsonProperty("use")
    private String Use;

    @JsonProperty("price")
    private String Price;

    @JsonProperty("side_effects")
    private String SideEffects;

    @JsonProperty("dosage_form")
    private String DosageForm;

    @JsonProperty("Scientific_Name")
    private String ScientificName;

    @JsonProperty("max_dose")
    private String MaxDose;

    @JsonProperty("administration")
    private String Administration;

    @JsonProperty("indications")
    private String Indications;

    @JsonProperty("precautions")
    private String Precautions;

    @JsonProperty("serious_effects")
    private String SeriousEffects;

    @JsonProperty("contraindications")
    private String Contraindications;

    @JsonProperty("Source_of_information")
    private String SourceOfInformation;

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getDosage() {
        return Dosage;
    }

    public void setDosage(String dosage) {
        Dosage = dosage;
    }

    public String getUse() {
        return Use;
    }

    public void setUse(String use) {
        Use = use;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getSideEffects() {
        return SideEffects;
    }

    public void setSideEffects(String sideEffects) {
        SideEffects = sideEffects;
    }

    public String getDosageForm() {
        return DosageForm;
    }

    public void setDosageForm(String dosageForm) {
        DosageForm = dosageForm;
    }

    public String getScientificName() {
        return ScientificName;
    }

    public void setScientificName(String scientificName) {
        ScientificName = scientificName;
    }

    public String getMaxDose() {
        return MaxDose;
    }

    public void setMaxDose(String maxDose) {
        MaxDose = maxDose;
    }

    public String getAdministration() {
        return Administration;
    }

    public void setAdministration(String administration) {
        this.Administration = administration;
    }

    public String getIndications() {
        return Indications;
    }

    public void setIndications(String indications) {
        Indications = indications;
    }

    public String getPrecautions() {
        return Precautions;
    }

    public void setPrecautions(String precautions) {
        Precautions = precautions;
    }

    public String getSeriousEffects() {
        return SeriousEffects;
    }

    public void setSeriousEffects(String seriousEffects) {
        SeriousEffects = seriousEffects;
    }

    public String getContraindications() {
        return Contraindications;
    }

    public void setContraindications(String contraindications) {
        Contraindications = contraindications;
    }

    public String getSourceOfInformation() {
        return SourceOfInformation;
    }

    public void setSourceOfInformation(String sourceOfInformation) {
        SourceOfInformation = sourceOfInformation;
    }
}

