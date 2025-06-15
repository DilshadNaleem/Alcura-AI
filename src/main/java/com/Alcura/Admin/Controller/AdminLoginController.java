package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.AdminLoginAuthImpl;
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
@RequestMapping("/Admin")
public class AdminLoginController
{
    private final AdminLoginAuthImpl adminLoginAuth;

    public AdminLoginController(AdminLoginAuthImpl adminLoginAuth)
    {
        this.adminLoginAuth = adminLoginAuth;
    }

    @PostMapping("/Login")
    public void loginAdmin(@ModelAttribute AdminLoginRequest request, HttpSession session,
                           HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");

        try
        {
            ResponseEntity<String> result = adminLoginAuth.loginAdmin(request,session);

            if(result.getStatusCode() == HttpStatus.FOUND)
            {
                out.println("alert('Login Successful!');");
                out.println("window.location.href = '/Admin/Dashboard';");
            }
            else if (result.getStatusCode() == HttpStatus.FORBIDDEN)
            {
                String message = result.getBody() != null ?
                        result.getBody() : "Your Account is not verified. Please Verify!";
                out.println("alert('" + message + "');");
                out.println("window.location.href = '/Admin/Signing';");
            }
            else
            {
                out.println("alert('Invalid email or Password. Please Try Again!');");
                out.println("window.location.href = '/Admin/Signing';");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            out.println("alert('An Error Occurred During login. Please try again!');");
            out.println("window.location.href = '/Admin/Signing';");
        }
        finally
        {
            out.println("</script>");
            out.close();
        }
    }
}