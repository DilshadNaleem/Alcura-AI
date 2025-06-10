package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.CusLoginAuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/Customer")
public class LoginController
{
    private final CusLoginAuthServiceImpl customerAuthService;

    public LoginController(CusLoginAuthServiceImpl customerAuthService)
    {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/Login")
    public void loginCustomer(@ModelAttribute LoginRequest request,
                              HttpServletResponse response,
                              HttpSession session) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");

        try {
            ResponseEntity<String> result = customerAuthService.loginCustomer(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND) {
                // Successful login
                out.println("var msg = new SpeechSynthesisUtterance('Login successful');");
                out.println("window.speechSynthesis.speak(msg);");
                out.println("window.location.href = '/Customer/Dashboard';");
            } else if (result.getStatusCode() == HttpStatus.FORBIDDEN) {
                // Account not verified
                String message = result.getBody() != null ?
                        result.getBody() : "Your account is not yet verified. Please check your email.";
                out.println("var msg = new SpeechSynthesisUtterance('" + escapeJavaScript(message) + "');");
                out.println("window.speechSynthesis.speak(msg);");
                out.println("alert('" + escapeJavaScript(message) + "');");
                out.println("window.location.href = '/Customer/Signing';");
            } else {
                // Failed login
                String message = "Invalid email or password. Please try again.";
                out.println("var msg = new SpeechSynthesisUtterance('" + message + "');");
                out.println("window.speechSynthesis.speak(msg);");
                out.println("alert('" + message + "');");
                out.println("window.location.href = '/Customer/Signing';");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred during login. Please try again later.";
            out.println("var msg = new SpeechSynthesisUtterance('" + errorMessage + "');");
            out.println("window.speechSynthesis.speak(msg);");
            out.println("alert('" + errorMessage + "');");
            out.println("window.location.href = '/Customer/Signing';");
        } finally {
            out.println("</script>");
            out.close();
        }
    }
    private String escapeJavaScript(String input) {
        if (input == null) return "";
        return input.replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

}
