package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.FactoryPattern.LoginFactory.LoginHandlerFactory;
import com.Alcura.FactoryPattern.Service.LoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;

@Controller
@RequestMapping("/Doctor")
public class DRLoginController {
    private final DRLoginAuthServiceImpl drLoginAuthService;
    private final DoctorRepository doctorRepository;

    public DRLoginController(DRLoginAuthServiceImpl drLoginAuthService,
                             DoctorRepository doctorRepository) {
        this.drLoginAuthService = drLoginAuthService;
        this.doctorRepository = doctorRepository;
    }

    @PostMapping("/Login")
    public void Login(@ModelAttribute DRLoginRequest request,
                      HttpServletResponse response,
                      HttpSession session) throws IOException {
        LoginHandler handler = LoginHandlerFactory.createHandler(
                "doctor",
                drLoginAuthService,
                request,
                response,
                session,
                doctorRepository
        );
        try {
            handler.handleLogin(response, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}