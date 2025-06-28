package com.Alcura.Admin.DTO;

public class ViewAllDoctors
{
    private String unique_id;
    private String first_name;
    private String last_name;
    private String contact_number;
    private String email;
    private String government_hospital;
    private String nic;
    private String other_specialization;
    private String specialist;
    private String special_note;
    private String status;
    private String special_info;

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGovernment_hospital() {
        return government_hospital;
    }

    public void setGovernment_hospital(String government_hospital) {
        this.government_hospital = government_hospital;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getOther_specialization() {
        return other_specialization;
    }

    public void setOther_specialization(String other_specialization) {
        this.other_specialization = other_specialization;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getSpecial_note() {
        return special_note;
    }

    public void setSpecial_note(String special_note) {
        this.special_note = special_note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecial_info() {
        return special_info;
    }

    public void setSpecial_info(String special_info) {
        this.special_info = special_info;
    }


    public ViewAllDoctors(String uniqueId, String firstName, String lastName,
                          String contactNumber, String email,
                          String government_Hospitals, String nic,
                          String other_specialization, String specialist,
                          String special_note, String status,
                          String specialist_info) {
        this.unique_id = uniqueId;
        this.first_name = firstName;
        this.last_name = lastName;
        this.contact_number = contactNumber;
        this.email = email;
        this.government_hospital = government_Hospitals;
        this.nic = nic;
        this.other_specialization = other_specialization;
        this.specialist = specialist;
        this.special_note = special_note;
        this.status = status;
        this.special_info = specialist_info;

    }
}
