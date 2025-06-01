package com.Alcura.Customer.Service.Interfaces;

import jakarta.servlet.http.HttpSession;

public interface OtpService
{
    String generateOtp();

    void storeOtp (HttpSession session, String email, String otp);
    boolean validateOtp (HttpSession session, String email, String otp);
}
