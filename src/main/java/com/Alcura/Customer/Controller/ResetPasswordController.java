package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.hashPassword;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class ResetPasswordController
{
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private hashPassword hashPassword;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                        HttpSession session,
                                        HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String sessiontoken = (String) session.getAttribute("token");
        if (sessiontoken == null || !sessiontoken.equals(token))
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Invalid or expired token Please try again!');");
            out.println("window.location.href = '/Customer/Signing';");
            out.println("</script>");
            return null;
        }

        return "/Customer/reset_password_form";
    }


    @PostMapping("reset-password")
    public void processResetPassword (@RequestParam("password") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      HttpSession session,
                                      HttpServletResponse response) throws  IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if(!newPassword.equals(confirmPassword))
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Password do not match');");
            out.println("window.location.href = 'reset-password?token=" + session.getAttribute("token") +  "';");
            out.println("</script>");
            return;
        }

        String email = (String)  session.getAttribute("email");

        if(email == null || email.isEmpty())
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('Session Expired!. Please Try again!');");
            out.println("window.location.href = '/Customer/recover_psw.html';");
            out.println("</script>");
            return;
        }

        Customer customer = customerRepository.findByEmailAndStatus(email,1);
        if(customer != null)
        {
            String hashpsw = hashPassword.hashPassword(newPassword);
            customer.setPassword(hashpsw);
            customerRepository.save(customer);

            session.removeAttribute("token");
            session.removeAttribute("email");

            out.println("<script type = 'text/javascript'>");
            out.println("alert('Password updated Successfully!');");
            out.println("window.location.href = '/Customer/Signing';");
            out.println("</script>");
            return;
        }
        else
        {
            out.println("<script type = 'text/javascript'>");
            out.println("alert('User not found');");
            out.println("window.location.href = '/Customer/Signing';");
            out.println("</script>");
            return;
        }
    }
}
