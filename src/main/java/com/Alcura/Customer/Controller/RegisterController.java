package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.RegisterRequest;

import com.Alcura.Customer.Service.Interfaces.CustomerAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/")
public class RegisterController
{
    private  final CustomerAuthService customerAuthService;


    public RegisterController (CustomerAuthService customerAuthService)
    {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/Alcura/CustomerRegister")
    public void registerCustomer (@ModelAttribute RegisterRequest request,
                                  HttpSession session, HttpServletResponse response) throws IOException
    {
        ResponseEntity<String> result = customerAuthService.registerCustomer(request,session);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type = 'text/javascript'>");

        if(result.getStatusCode() == HttpStatus.CREATED)
        {
            out.println("alert('Registration Success Please Verify');");
            out.println("window.location.href = '/Customer/verification';");
        }
        else if (result.getStatusCode() == HttpStatus.BAD_REQUEST)
        {
            out.println("alert('Email Already Exists!');");
            out.println("window.location.href = '/Customer/Signing';");
        }
        else
        {
            out.println("alert('" + result.getBody() + "');");
            out.println("window.location.href = '/Customer/Signing';");
        }

        out.println("</script>");
        out.close();
    }
}
