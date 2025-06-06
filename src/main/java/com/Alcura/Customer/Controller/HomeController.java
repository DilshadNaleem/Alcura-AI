package com.Alcura.Customer.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String home()
    {
        return "redirect:/Customer/Signing.html";
    }

    @GetMapping("/Customer/verification")
    public String showverificationPage()
    {
        return "/Customer/verification";
    }

    @GetMapping("/Customer/Dashboard")
    public String dashboard (HttpSession session)
    {
        if (session.getAttribute("email") == null)
        {
            return "redirect:/Customer/Signing.html";
        }
        return  "/Customer/Dashboard";
    }


    @GetMapping("/Customer/editProfile")
    public String profile()
    {
        return "/Customer/edit_profile";
    }

    @GetMapping("/Customer/SOS")
    public String sos()
    {
        return "/Customer/sos";
    }

    @GetMapping("/Customer/SOSDashboard")
    public String SOSDashboard()
    {
        return "/Customer/SOSDashboard";
    }

    @GetMapping("/Customer/FaceLogin")
    public String facelogin()
    {
        return "/Customer/face-login";
    }

    @GetMapping("/Customer/FaceEnrollment")
    public String faceenrollment()
    {
        return "/Customer/face_enrollment";
    }

    @GetMapping("/Customer/UpdateFace")
    public String UpdateFace()
    {
        return "/Customer/Update_Face";
    }

    @GetMapping("/Customer/Model")
    public String model()
    {
        return "/Customer/LlamaModel";
    }

    @GetMapping("/Customer/MedicineClassifier")
    public String PillModel()
    {
        return "/Customer/MedicineClassifier";
    }

    @GetMapping("/Customer/DiseaseClassifier")
    public String Diseae()
    {
        return "/Customer/DiseaseClassifier";
    }

    @GetMapping("/Customer/DiseaseSymptomChecker")
    public String DiseaseSymptom()
    {
        return "/Customer/DiseaseSymptom";
    }
}
