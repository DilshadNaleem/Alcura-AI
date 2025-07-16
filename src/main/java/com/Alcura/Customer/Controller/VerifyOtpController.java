package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Service.Interfaces.CustomerAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/")
public class VerifyOtpController
{
    private final CustomerAuthService customerAuthService;

    public VerifyOtpController (CustomerAuthService customerAuthService)
    {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/Alcura/verifyOtp")
    public void verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body><script type='text/javascript'>");

            ResponseEntity<String> verificationResult = customerAuthService.verifyOtp(otp, session);
            String message;
            String redirectUrl;

            if (verificationResult.getStatusCode() == HttpStatus.OK) {
                message = "Account verified successfully! Please login.";
                redirectUrl = "'/Customer/Signing'";
            } else {
                message = verificationResult.getBody() != null ?
                        verificationResult.getBody() : "Account verification failed. Please try again.";
                redirectUrl = "'/Customer/verification'";
            }

            // Escape JavaScript strings
            message = message.replace("'", "\\'");
            out.println("alert('" + message + "');");
            out.println("window.location.href = " + redirectUrl + ";");

            out.println("</script></body></html>");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("<html><body><script type='text/javascript'>");
                out.println("alert('An error occurred during verification.');");
                out.println("window.location.href = '/Customer/verification';");
                out.println("</script></body></html>");
            }
        }
    }
}
