package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import com.Alcura.Customer.Service.hashPassword;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class AdminResetPasswordController
{
    private final AdminRepository adminRepository;
    private final hashPassword hashPassword;

    public AdminResetPasswordController(AdminRepository adminRepository,
                                        hashPassword hashPassword)
    {
        this.adminRepository = adminRepository;
        this.hashPassword = hashPassword;
    }

    @GetMapping("Admin-Reset-password")
    public String showResetPassword(@RequestParam("token") String token,
                                    HttpSession session,
                                    HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();

            String sessionToken = (String) session.getAttribute("token");

            if (sessionToken == null) {
                writer.println("<script type = text/javascript>");
                writer.println("alert('Invalid token or Expired token');");
                writer.println("window.location.href = '/Admin-Reset-password'");
                writer.println("</script>");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage().toString();
        }
        return "/Admin/AdminResetPassword";
    }

        @PostMapping("Reset-Password")
                public void processResetPassowrd (@RequestParam("password") String newPassword,
                                                  @RequestParam("confirmPassword") String confirmPassword,
        HttpSession session, HttpServletResponse response) throws IOException
        {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            if(!newPassword.equals(confirmPassword))
            {
                out.println("<script type = 'text/javascript'>");
                out.println("alert('Password do not match');");
                out.println("winodw.location.href = 'Reset-Password?token= "+ session.getAttribute("token") + "';");
                return;
        }

            String email = (String) session.getAttribute("email");

            if(email == null)
            {
                out.println("<script type = 'text/javascript'>");
                out.println("alert('Session Expired! Please Try Again!');");
                out.println("window.location.href = '/Admin/Signing';");
                out.println("</script>");
                return;
            }

            Admin admin = adminRepository.findByEmailAndStatus(email, 1);
            if(admin != null)
            {
                String hashpsw = hashPassword.hashPassword(newPassword);
                admin.setPassword(hashpsw);
                adminRepository.save(admin);

                session.removeAttribute("token");
                session.removeAttribute("email");

                out.println("<script type = 'text/javascript'>");
                out.println("alert('Password Updated Successfully!');");
                out.println("window.location.href = '/Admin/Signing';");
                out.println("</script>");
                return;
            }

            else
            {
                out.println("<script type = 'text/javascript'>");
                out.println("alert('User not found!');");
                out.println("window.location.href = '/Admin/Signing';");
                out.println("</script>");
                return;
            }
    }
}
