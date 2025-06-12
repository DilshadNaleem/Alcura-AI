package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.AdminRegisterRequest;
import com.Alcura.Admin.Service.Interfaces.AdminAuthService;
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
@RequestMapping("/Admin/Register")
public class AdminRegisterController {
    private final AdminAuthService adminAuthService;

    public AdminRegisterController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping
    public void registerAdmin(@ModelAttribute AdminRegisterRequest request,
                              HttpSession session,
                              HttpServletResponse response) throws IOException {
        ResponseEntity<String> result = adminAuthService.registerAdmin(request, session);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");

        if (result.getStatusCode() == HttpStatus.CREATED) {
            out.println("alert('Registration successful! Please verify your email.');");
            out.println("window.location.href = '/Admin/verification';");
        } else if (result.getStatusCode() == HttpStatus.BAD_REQUEST) {
            out.println("alert('" + result.getBody() + "');");
            out.println("window.location.href = '/Admin/Signing';");
        } else {
            out.println("alert('Registration failed: " + result.getBody() + "');");
            out.println("window.location.href = '/Admin/Signing';");
        }

        out.println("</script>");
        out.close();
    }
}