package com.Alcura.Customer.DTO;

public class DiseaseClassificationResult
{
    private String predictedClass;
    private double confidence;
    private DiseaseInfo diseaseInfo;

    public DiseaseClassificationResult(String predictedClass, double confidence, DiseaseInfo diseaseInfo)
    {
        this.predictedClass = predictedClass;
        this.confidence = confidence;
        this.diseaseInfo = diseaseInfo;
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

    public DiseaseInfo getDiseaseInfo() {
        return diseaseInfo;
    }

    public void setDiseaseInfo(DiseaseInfo diseaseInfo) {
        this.diseaseInfo = diseaseInfo;
    }
}
