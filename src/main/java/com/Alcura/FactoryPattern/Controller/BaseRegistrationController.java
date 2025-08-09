package com.Alcura.FactoryPattern.Controller;

import com.Alcura.FactoryPattern.Service.RegistrationService;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseRegistrationController
{
    private final RegistrationServiceFactory serviceFactory;
    private final String userType;
    private final String successRedirect;
    private final String failureRedirect;

    protected BaseRegistrationController (RegistrationServiceFactory serviceFactory,
                                          String userType,
                                          String successRedirect,
                                          String failureRedirect)

    {
        this.failureRedirect = failureRedirect;
        this.serviceFactory = serviceFactory;
        this.successRedirect = successRedirect;
        this.userType = userType;
    }
    protected  void handleRegistration (Object request,
                                        HttpSession session,
                                        HttpServletResponse response) throws IOException {
        RegistrationService service = serviceFactory.getService(userType);
        ResponseEntity<String> result = service.register(request, session);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type = 'text/javascript'>");

        if (result.getStatusCode() == HttpStatus.CREATED) {
            out.println("alert('Registration successful! Please verify your email.');");
            out.println("window.location.href = '" + successRedirect + "';");
        } else if (result.getStatusCode() == HttpStatus.BAD_REQUEST) {
            out.println("alert('" + result.getBody() + "');");
            out.println("window.location.href = '" + failureRedirect + "';");
        } else {
            out.println("alert('Registration failed: " + result.getBody() + "');");
            out.println("window.location.href = '" + failureRedirect + "';");
        }

        out.println("</script>");
        out.close();
    }
}
