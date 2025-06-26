package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/Doctor")
public class DRLoginController
{
    private final DRLoginAuthServiceImpl drLoginAuthService;

    public DRLoginController(DRLoginAuthServiceImpl drLoginAuthService)
    {
        this.drLoginAuthService = drLoginAuthService;
    }

    @PostMapping("/Login")
    public void Login(@ModelAttribute DRLoginRequest request,
                      HttpServletResponse response,
                      HttpSession session) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type = 'text/javascript'>");

        try
        {
            ResponseEntity<String> result = drLoginAuthService.loginCustomer(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND)
            {
                out.println("alert('Login Successful');");
                out.println("window.location.href = '/Doctor/Dashboard';");
            }
            else if (result.getStatusCode() == HttpStatus.FORBIDDEN)
            {
                String message = result.getBody() != null ?
                        result.getBody() : "Your Account is not verified. Please Verify!";
                out.println("alert('" + message + "');");
                out.println("window.location.href = '/Doctor/Signing';");
            }
            else
            {
                out.println("alert('Invalid email or Password. Please Try Again!');");
                out.println("window.location.href = '/Doctor/Signing';");
            }
        }
        catch (Exception e)
        {
            out.println("alert('An Error Occurred during login please try later');");
            out.println("window.location.href = '/Doctor/Signing'");
        } finally {
            out.println("</script>");
            out.close();
        }
    }
}
