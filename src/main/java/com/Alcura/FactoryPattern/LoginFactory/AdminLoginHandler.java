package com.Alcura.FactoryPattern.LoginFactory;

import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.AdminLoginAuthImpl;
import com.Alcura.FactoryPattern.ServiceFactory.BaseLoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class AdminLoginHandler extends BaseLoginHandler
{
    private final AdminLoginAuthImpl authService;
    private final AdminLoginRequest request;

    public AdminLoginHandler(AdminLoginAuthImpl authService, AdminLoginRequest request,
                             HttpServletResponse response, HttpSession session) throws IOException {
        super(response, session, "/Admin/Dashboard", "/Admin/Signing");
        this.authService = authService;
        this.request = request;
    }

    @Override
    public void handleLogin(HttpServletResponse response, HttpSession session) throws Exception {
        try {
            ResponseEntity<String> result = authService.loginAdmin(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND) {
                handleSuccess();
            } else if (result.getStatusCode() == HttpStatus.FORBIDDEN) {
                String message = result.getBody() != null ?
                        result.getBody() : "Your Account is not verified. Please Verify!";
                handleForbidden(message);
            } else {
                handleFailure("Invalid email or Password. Please Try Again!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError();
        } finally {
            close();
        }
    }
}
