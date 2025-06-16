package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.DiseaseInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    @GetMapping("/Admin/Signing")
    public String Login()
    {
        return "/Admin/Admin_Signing";
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

    @GetMapping("/Admin/SOS")
    public String sosview()
    {
        return "/Admin/SOSView";
    }

    @GetMapping("/Admin/Emergency")
    public String emergency()
    {
        return "/Admin/Emergencies";
    }

    @GetMapping("/Admin/AddDisease")
    public String addDisease(Model model)
    {
        model.addAttribute("diseaseInfo", new DiseaseInfo());
        return "/Admin/Add_Disease";
    }

}
