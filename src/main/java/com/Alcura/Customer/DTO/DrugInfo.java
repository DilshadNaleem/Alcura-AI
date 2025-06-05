package com.Alcura.Customer.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DrugInfo
{
    @JsonProperty("class_name")
    private String className;

    @JsonProperty("dosage")
    private String dosage;

    @JsonProperty("Scientific_Name")
    private String scientificname;

    @JsonProperty("use")
    private String use;

    @JsonProperty("price")
    private String price;

    @JsonProperty("side_effects")
    private String sideEffects;

    @JsonProperty("dosage_form")
    private String dosageForm;

    @JsonProperty("max_dose")
    private String maxDose;

    @JsonProperty("administration")
    private String administration;

    @JsonProperty("indications")
    private String indications;

    @JsonProperty("precautions")
    private String precautions;

    @JsonProperty("serious_effects")
    private String seriousEffects;

    @JsonProperty("contraindications")
    private String contraindications;

    @JsonProperty("Source_of_information")
    private String source_of_information;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public DrugInfo() {}

    public String getScientificname() {
        return scientificname;
    }

    public void setScientificname(String scientificname) {
        this.scientificname = scientificname;
    }

    public String getSource_of_information() {
        return source_of_information;
    }

    public void setSource_of_information(String source_of_information) {
        this.source_of_information = source_of_information;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getMaxDose() {
        return maxDose;
    }

    public void setMaxDose(String maxDose) {
        this.maxDose = maxDose;
    }

    public String getAdministration() {
        return administration;
    }

    public void setAdministration(String administration) {
        this.administration = administration;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getSeriousEffects() {
        return seriousEffects;
    }

    public void setSeriousEffects(String seriousEffects) {
        this.seriousEffects = seriousEffects;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }


    public DrugInfo(String className, String dosage, String use, String price, String sideEffects, String dosageForm, String maxDose, String administration, String indications, String precautions, String seriousEffects, String contraindications) {
        this.className = className;
        this.dosage = dosage;
        this.use = use;
        this.price = price;
        this.sideEffects = sideEffects;
        this.dosageForm = dosageForm;
        this.maxDose = maxDose;
        this.administration = administration;
        this.indications = indications;
        this.precautions = precautions;
        this.seriousEffects = seriousEffects;
        this.contraindications = contraindications;
    }

}
