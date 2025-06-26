package com.Alcura.Doctor.Controller;

import com.Alcura.Customer.Service.Interfaces.EmailService;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Controller
@RequestMapping("/Doctor")
public class DrForgotPassword
{
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    EmailService emailService;

    @Autowired
    DoctorRepository doctorRepository;

    @PostMapping("/ForgotPassword")
    public void processForgotPassword(@RequestParam("email") String email,
                                      HttpSession session,
                                      HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Doctor doctor = doctorRepository.findByEmailAndStatus(email,1);

        if (doctor == null)
        {
            out.println("<script type='text/javascript'>");
            out.println("alert('Email not found or account inactive')");
            out.println("window.location.href = '/Doctor/RecoverPassword';");
            out.println("</script>");
            return;
        }

        String token = UUID.randomUUID().toString();
        session.setAttribute("token", token);
        session.setAttribute("email", email);

        String resetLink = "http://localhost:8081/Doctor/ResetPasswordForm?token=" + token;

        emailService.sendPasswordResetEmail(email, resetLink);

        out.println("<script type='text/javascript'>");
        out.println("alert('Password Reset Link send to the email');");
        out.println("window.location.href = '/Doctor/Signing'");
        out.println("</script>");
    }
}
