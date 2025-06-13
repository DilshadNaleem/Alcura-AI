package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import com.Alcura.Customer.Service.Interfaces.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@RestController
@RequestMapping("/Admin/")
public class AdminPasswordController {
    private final JavaMailSender javaMailSender;
    private final AdminRepository adminRepository;
    private final EmailService emailService;

    public AdminPasswordController(JavaMailSender javaMailSender,
                                   AdminRepository adminRepository,
                                   EmailService emailService)
    {
        this.adminRepository = adminRepository;
        this.emailService = emailService;
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/ForgotPassword")
    public  void processForgotPassword(@RequestParam("email") String email,
                                       HttpSession session,
                                       HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Admin admin = adminRepository.findByEmailAndStatus(email,1);

        if(admin == null)
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Email not found or Account Inactive');");
            out.println("window.location.href = '/Admin/ResetPassword';");
            out.println("</script>");
            return;
        }

        String token = UUID.randomUUID().toString();
        session.setAttribute("token", token);
        session.setAttribute("email", email);

        String resetLink = "http://localhost:8081/Admin-Reset-password?token=" + token;

        emailService.sendPasswordResetEmail(email,resetLink);

        out.println("<script type='text/javascript'>");
        out.println("alert('Password reset Link send to the email');");
        out.println("window.location.href = '/Admin/Signing';");
        out.println("</script>");
        out.close();
    }
}
