package com.Alcura.Admin.Controller.Hospital;

import com.Alcura.Admin.Service.DeleteHospitalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;

@RequestMapping("/Admin")
@RestController
public class DeleteHospitalController
{
    private final DeleteHospitalService deleteHospitalService;

    public DeleteHospitalController (DeleteHospitalService deleteHospitalService)
    {
        this.deleteHospitalService = deleteHospitalService;
    }

    @RequestMapping("/DeleteHospitals/{id}")
    public void DeleteHospital(@PathVariable String  id, HttpServletResponse response, HttpSession session) throws IOException
    {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");

        String email = (String) session.getAttribute("email");
        if (email == null || email.isEmpty())
        {
            writer.println("<script type = 'text/javascript'>");
            writer.println("alert('Login First');");
            writer.println("window.location.href = '/Admin/Signing';");
            writer.println("</script>");
            return;
        }
        deleteHospitalService.deleteHospitalByUniqueId(id,writer);
    }
}
