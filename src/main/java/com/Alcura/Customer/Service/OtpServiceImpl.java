package com.Alcura.Customer.Service;

import com.Alcura.Customer.Service.Interfaces.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService
{
    private static final String OTP_ATTRIBUTE = "verificationOtp";
    private static final String OTP_EMAIL_ATTRIBUTE = "verificationEmail";
    
    public String generateOtp()
    {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    @Override
    public void storeOtp(HttpSession session, String email, String otp) {
        session.setAttribute(OTP_EMAIL_ATTRIBUTE,email);
        session.setAttribute(OTP_ATTRIBUTE,otp);
        
        session.setMaxInactiveInterval(300);
    }

    @Override
    public boolean validateOtp(HttpSession session, String email, String otp) {
        String sessionEmail = (String) session.getAttribute(OTP_EMAIL_ATTRIBUTE);
        String sessionOtp = (String)  session.getAttribute(OTP_ATTRIBUTE);
        
        return email.equals(sessionEmail) && otp.equals(sessionOtp) && session != null;
    }
}
