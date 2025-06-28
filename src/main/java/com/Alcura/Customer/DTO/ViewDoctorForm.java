package com.Alcura.Customer.DTO;

public class ViewDoctorForm
{
    private String first_name;
    private String last_name;
    private String specialist;
    private String experience;;
    private String special_info;
    private byte[] image;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
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

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }


    public String getSpecial_info() {
        return special_info;
    }

    public void setSpecial_info(String special_info) {
        this.special_info = special_info;
    }

    public ViewDoctorForm(String firstName, String lastName,
                          String specialist, String experience,
                          String specialist_info, byte[] image,
                          String email)
    {
        this.first_name = firstName;
        this.last_name = lastName;
        this.experience = experience;
        this.specialist = specialist;
        this.special_info = specialist_info;
        this.image = image;
        this.email = email;
    }

}
