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
}
