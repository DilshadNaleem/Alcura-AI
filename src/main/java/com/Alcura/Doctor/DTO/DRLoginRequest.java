package com.Alcura.Doctor.DTO;

public class DRLoginRequest
{
    private String email;
    private String SigningPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSigningPassword() {
        return SigningPassword;
    }

    public void setSigningPassword(String signingPassword) {
        SigningPassword = signingPassword;
    }
}
