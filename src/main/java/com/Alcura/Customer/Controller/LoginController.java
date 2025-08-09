package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.CusLoginAuthServiceImpl;
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
@RequestMapping("/Customer")
public class LoginController {
    private final CusLoginAuthServiceImpl customerAuthService;

    public LoginController(CusLoginAuthServiceImpl customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/Login")
    public void loginCustomer(@ModelAttribute LoginRequest request,
                              HttpServletResponse response,
                              HttpSession session) throws IOException {
        LoginHandler handler = LoginHandlerFactory.createHandler(
                "customer",
                customerAuthService,
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