package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.FactoryPattern.Controller.BaseRegistrationController;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/Doctor")
@RestController
public class DoctorRegisterController extends BaseRegistrationController
{
    public DoctorRegisterController(RegistrationServiceFactory serviceFactory) {
        super(serviceFactory, "doctor", "/Doctor/Verification", "/Doctor/Signing");
    }

    @PostMapping("/Register")
    public void registerDoctor(@ModelAttribute DoctorRegisterRequest request,
                               HttpSession session,
                               HttpServletResponse response) throws IOException {
        handleRegistration(request, session, response);
    }
}
