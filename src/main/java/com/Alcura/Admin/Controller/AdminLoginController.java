package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.AdminLoginAuthImpl;

import com.Alcura.FactoryPattern.LoginFactory.LoginHandlerFactory;
import com.Alcura.FactoryPattern.Service.LoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/Admin")
public class AdminLoginController {
    private final AdminLoginAuthImpl adminLoginAuth;

    public AdminLoginController(AdminLoginAuthImpl adminLoginAuth) {
        this.adminLoginAuth = adminLoginAuth;
    }

    @PostMapping("/Login")
    public void loginAdmin(@ModelAttribute AdminLoginRequest request,
                           HttpSession session,
                           HttpServletResponse response) throws IOException {
        LoginHandler handler = LoginHandlerFactory.createHandler(
                "admin",
                adminLoginAuth,
                request,
                response,
                session,
                null
        );
        try {
            handler.handleLogin(response, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}