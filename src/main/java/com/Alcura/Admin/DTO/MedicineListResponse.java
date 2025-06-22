package com.Alcura.Admin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MedicineListResponse {

    @JsonProperty("medicine")
    private List<String> medicine;

    public MedicineListResponse()
    {
    }

    public MedicineListResponse (List<String> medicine)
    {
        this.medicine = medicine;
    }

    public List<String> getAllMedicine(){
        return medicine;
    }

    public void setMedicines(List<String> medicine)
    {
        this.medicine = medicine;
    }
}
