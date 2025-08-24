package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.Service.Interfaces.DoctorAuthService;
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
@RequestMapping("/Doctor")
public class DoctorVerifyOtp
{
    private final DoctorAuthService doctorAdminAuthService;

    public DoctorVerifyOtp(DoctorAuthService doctorAdminAuthService)
    {
        this.doctorAdminAuthService = doctorAdminAuthService;
    }

    @PostMapping("/VerifyOtp")
    public void verifyOtp(@RequestParam String otp,
                          HttpSession session,
                          HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");

        try(PrintWriter out = response.getWriter())
        {
            out.println("<script type = 'text/javascript'>");

            ResponseEntity<String> verificationResult = doctorAdminAuthService.verifyOtp(otp, session);
            String message;
            String redirectUrl;

            if (verificationResult.getStatusCode() == HttpStatus.OK)
            {
                message = "Account verified Successfully! Please Login";
                redirectUrl = "'/Doctor/Signing'";
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else
            {
                message = verificationResult.getBody() != null ? verificationResult.getBody() :
                        "Account verification failed.";
                redirectUrl = "'/Doctor/Signing'";
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            // Escape single quotes for JavaScript
            message = message.replace("'", "\\'");
            out.println("alert('" + message + "');");
            out.println("window.location.href = " + redirectUrl + ";");

            out.println("</script>");
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter out = response.getWriter())
            {
                out.println("<script type = 'text/javascript'>");
                out.println("alert('An Error occurred during verification');");
                out.println("window.location.href = '/Doctor/Verification';");
                out.println("</script>");

            }
        }
    }
}