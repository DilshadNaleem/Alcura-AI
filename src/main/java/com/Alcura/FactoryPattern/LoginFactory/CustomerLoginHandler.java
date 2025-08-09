package com.Alcura.FactoryPattern.LoginFactory;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.CusLoginAuthServiceImpl;
import com.Alcura.FactoryPattern.ServiceFactory.BaseLoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class CustomerLoginHandler extends BaseLoginHandler
{
    private final CusLoginAuthServiceImpl authService;
    private final LoginRequest request;

    public CustomerLoginHandler(CusLoginAuthServiceImpl authService, LoginRequest request,
                                HttpServletResponse response, HttpSession session) throws IOException {
        super(response, session, "/Customer/Dashboard", "/Customer/Signing");
        this.authService = authService;
        this.request = request;
    }

    @Override
    public void handleLogin(HttpServletResponse response, HttpSession session) throws Exception {
        try {
            ResponseEntity<String> result = authService.loginCustomer(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND) {
                out.println("var msg = new SpeechSynthesisUtterance('Login successful');");
                out.println("window.speechSynthesis.speak(msg);");
                out.println("window.location.href = '" + redirectSuccess + "';");
            } else if (result.getStatusCode() == HttpStatus.FORBIDDEN) {
                String message = result.getBody() != null ?
                        result.getBody() : "Your account is not yet verified. Please check your email.";
                out.println("var msg = new SpeechSynthesisUtterance('" + escapeJavaScript(message) + "');");
                out.println("window.speechSynthesis.speak(msg);");
                handleForbidden(message);
            } else {
                String message = "Invalid email or password. Please try again.";
                out.println("var msg = new SpeechSynthesisUtterance('" + message + "');");
                out.println("window.speechSynthesis.speak(msg);");
                handleFailure(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred during login. Please try again later.";
            out.println("var msg = new SpeechSynthesisUtterance('" + errorMessage + "');");
            out.println("window.speechSynthesis.speak(msg);");
            handleError();
        } finally {
            close();
        }
    }
}
