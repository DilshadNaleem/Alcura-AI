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
        return "/Admin/DiseasePredictionModel/Add_Disease";
    }

    @GetMapping("/Admin/PredictDisease")
    public String predictDisease()
    {
        return "/Admin/DiseasePredictionModel/PredictDisease";
    }

    @GetMapping("/Admin/DiseaseModelDashboard")
    public String diseaseModel()
    {
        return "/Admin/DiseasePredictionModel/DiseaseModelDashboard";
    }

    @GetMapping("/Admin/MedicineModelDashboard")
    public String medicine()
    {
        return "/Admin/MedicinePredictionModel/MedicineModelDashboard";
    }


    @GetMapping("/Admin/FaceEnrollment")
    public String enrollment()
    {
        return "/Admin/AdminFace_Enrollment";
    }

    @GetMapping("/Admin/FaceLogin")
    public String update()
    {
        return "/Admin/Admin_face-login";
    }

}
