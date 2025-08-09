package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.RegisterRequest;

import com.Alcura.FactoryPattern.Controller.BaseRegistrationController;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class RegisterController extends BaseRegistrationController
{
   public RegisterController(RegistrationServiceFactory serviceFactory)
   {
       super ( serviceFactory, "customer", "/Customer/verification", "/Customer/Signing");
   }


    @PostMapping("/Alcura/CustomerRegister")
    public void registerCustomer(@ModelAttribute RegisterRequest request,
                                 HttpSession session,
                                 HttpServletResponse response) throws IOException {
        handleRegistration(request, session, response);
    }
}
