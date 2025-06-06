package com.Alcura.Customer.DTO;

public class MedicineClassificationResult
{
    private String predictedClass;
    private double confidence;
    private DrugInfo drugInfo;

    public MedicineClassificationResult(String predictedClass, double confidence, DrugInfo drugInfo) {
        this.predictedClass = predictedClass;
        this.confidence = confidence;
        this.drugInfo = drugInfo;
    }

    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = predictedClass;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public DrugInfo getDrugInfo() {
        return drugInfo;
    }

    public void setDrugInfo(DrugInfo drugInfo) {
        this.drugInfo = drugInfo;
    }
}
