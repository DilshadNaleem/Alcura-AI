package com.Alcura.Doctor.Controller;

import com.Alcura.Customer.Service.hashPassword;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/Doctor")
public class DRResetPasswordController
{
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private hashPassword hashPasswordClass;

    @GetMapping("/ResetPassword")
    public String  showResetPasswordForm(@RequestParam("token") String token,
                                       HttpSession session,
                                       HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String SessionToken = (String)  session.getAttribute("token");

        if (SessionToken == null)
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Invalid or expired token. Please try Again!');");
            out.println("window.location.href = '/Doctor/Signing';");
            out.println("</script>");
            return null;
        }

        return "/Doctor/ResetPasswordForm";
    }


    @PostMapping("/ResetPassword")
    public void processResetPassword(@RequestParam("password") String password,
                                     @RequestParam("confirmPassword") String newPassword,
                                     HttpSession session,
                                     HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();


        if (!newPassword.equals(newPassword))
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Password do not match');");
            out.println("window.location.href = '/Doctor/ResetPasswordForm?token=" + session.getAttribute("token") +"';");
            out.println("</script>");
            return;
        }


        String email = (String) session.getAttribute("email");

        if (email == null || email.isEmpty())
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Session Expired! Please Try Again!');");
            out.println("window.location.href = '/Customer/ResetPasswordForm';");
            out.println("</script>");
            return;
        }

        Doctor doctor = doctorRepository.findByEmailAndStatus(email,1);
        if (doctor != null)
        {
            String hashPassword = hashPasswordClass.hashPassword(newPassword);
            doctor.setPassword(hashPassword);
            doctorRepository.save(doctor);

            session.removeAttribute("token");
            session.removeAttribute("email");

            out.println("<script type = 'text/javascript'>");
            out.println("alert('Password Updated Successfully!');");
            out.println("window.location.href = '/Doctor/Signing'");
            out.println("</script>");
            return;
        }

        else
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('User not Found');");
            out.println("window.location.href = '/Doctor/Signing';");
            out.println("<script>");
            return;
        }
    }
}
