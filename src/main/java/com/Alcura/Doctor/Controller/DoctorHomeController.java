package com.Alcura.Doctor.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorHomeController
{
    @GetMapping("/Doctor/Signing")
    public String Signing()
    {
        return "/Doctor/Signing";
    }

    @GetMapping("/Doctor/RecoverPassword")
    public String RecoverPassword()
    {
        return "/Doctor/DoctorRecoverPassword";
    }

    @GetMapping("/Doctor/ResetPasswordForm")
    public String ResetPassword()
    {
        return "/Doctor/ResetPassword";
    }

    @GetMapping("/Doctor/Verification")
    public String verifyOtp()
    {
        return "/Doctor/Verification";
    }

    @GetMapping("/Doctor/FaceEnrollment")
    public String enrollFace()
    {
        return "/Doctor/DrFace_Enrollment";
    }

    @GetMapping("/Doctor/FaceLogin")
    public String faceLogin()
    {
        return "/Doctor/DrFace_login";
    }

}
