package com.Alcura.Customer.DTO;

public class LoginRequest
{
    private String email;
    private String signinPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSigninPassword() {
        return signinPassword;
    }

    public void setSigninPassword(String signinPassword) {
        this.signinPassword = signinPassword;
    }
}
