package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.AdminRegisterRequest;
import com.Alcura.FactoryPattern.Controller.BaseRegistrationController;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/Admin/Register")
public class AdminRegisterController extends BaseRegistrationController
{
 public AdminRegisterController(RegistrationServiceFactory serviceFactory)
 {
     super(serviceFactory, "admin", "/Admin/verification", "/Admin/Signing");
 }

    @PostMapping
    public void registerAdmin(@ModelAttribute AdminRegisterRequest request,
                              HttpSession session,
                              HttpServletResponse response) throws IOException {
        handleRegistration(request, session, response);
    }
}