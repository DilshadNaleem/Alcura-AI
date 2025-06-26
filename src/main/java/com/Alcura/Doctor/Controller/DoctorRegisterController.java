package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.Doctor.Service.Interfaces.DoctorAuthService;
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

@RequestMapping("/Doctor")
@RestController
public class DoctorRegisterController
{
    private final DoctorAuthService doctorAdminAuthService;

    public DoctorRegisterController(DoctorAuthService doctorAdminAuthService)
    {
        this.doctorAdminAuthService = doctorAdminAuthService;
    }

    @PostMapping("/Register")
    public void registerDoctor(@ModelAttribute DoctorRegisterRequest request,
                               HttpSession session,
                               HttpServletResponse response) throws IOException
    {
        ResponseEntity<String> result = doctorAdminAuthService.registerDoctor(request,session);
        {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");

            if (result.getStatusCode() == HttpStatus.CREATED) {
                out.println("alert('Registration successful! Please verify your email.');");
                out.println("window.location.href = '/Doctor/Verification';");
            } else if (result.getStatusCode() == HttpStatus.BAD_REQUEST) {
                out.println("alert('" + result.getBody() + "');");
                out.println("window.location.href = '/Doctor/Signing';");
            } else {
                out.println("alert('Registration failed: " + result.getBody() + "');");
                out.println("window.location.href = '/Doctor/Signing';");
            }

            out.println("</script>");
            out.close();
        }
    }
}
