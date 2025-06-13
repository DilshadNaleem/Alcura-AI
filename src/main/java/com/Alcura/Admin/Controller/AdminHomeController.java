package com.Alcura.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    @GetMapping("/Admin/Signing")
    public String Login()
    {
        return "/Admin/Admin_Signing";
    }

    @GetMapping("/Admin/Dashboard")
    public String AdminDashboard()
    {
        return "/Admin/AdminDashboard";
    }

    @GetMapping("/Admin/verification")
    public String verification()
    {
        return "/Admin/Admin_verification";
    }

    @GetMapping("/Admin/RecoverPassword")
    public String recoverPassword()
    {
        return "/Admin/RecoverPassword";
    }

    @GetMapping("/Admin/ResetPassword")
    public String resetPassword()
    {
        return "/Admin/AdminResetPassword";
    }
}
