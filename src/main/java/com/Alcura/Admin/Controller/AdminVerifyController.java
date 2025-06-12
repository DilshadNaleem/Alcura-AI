package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Repository.AdminRepository;
import com.Alcura.Admin.Service.Interfaces.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class AdminVerifyController
{
    private final AdminAuthService adminAuthService;

    public AdminVerifyController(AdminAuthService adminAuthService)
    {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/Admin/verifyOtp")
    public void verifyOtp(@RequestParam String otp, HttpSession session,
                          HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        try(PrintWriter writer = response.getWriter())
        {
            writer.println("<script = 'text/javascript'>");

            ResponseEntity<String> verificationResult = adminAuthService.verifyOtp(otp, session);
            String message = "";
            String redirectUtl = "";

            if(verificationResult.getStatusCode() == HttpStatus.OK)
            {
                message = "Account verified Successfully! Please Login";
                redirectUtl = "'/Admin/Signing'";
            }
            else
            {
                message = verificationResult.getBody() != null ?
                verificationResult.getBody() : "Account verification failed!";
                redirectUtl = "'/Admin/verification'";
            }

            message = message.replace("'", "\\");
            writer.println("alert('"+message+ "');");
            writer.println("window.location.href = " + redirectUtl + ";");

            writer.println("</script>");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try(PrintWriter out = response.getWriter())
            {
                out.println("<html><body><script type='text/javascript'>");
                out.println("alert('An error occurred during verification.');");
                out.println("window.location.href = '/Admin/verification';");
                out.println("</script></body></html>");
            }
        }
    }
}
