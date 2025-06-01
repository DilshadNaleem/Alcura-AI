package com.Alcura.Customer.Controller;

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
}
