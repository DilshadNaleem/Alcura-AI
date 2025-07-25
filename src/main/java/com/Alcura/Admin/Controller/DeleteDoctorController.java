package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Service.DeleteDoctorService;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/Admin")
public class DeleteDoctorController
{
    @Autowired
    private final DeleteDoctorService doctorService;

    public DeleteDoctorController(DeleteDoctorService doctorService)
    {
        this.doctorService = doctorService;
    }

    @RequestMapping("/DeleteDoctor/{id}")
    public void DeleteDoctor(@PathVariable String id, HttpServletResponse response,
                             HttpSession session) throws IOException
    {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");

        String email = (String) session.getAttribute("email");
        if (email == null || email.isEmpty())
        {
            writer.println("<script> alert ('Session Expired'); window.location.href = '/Admin/Signing'; </script>");
            return;
        }

        doctorService.deleteDoctorByUniqueId(id, writer);
    }
}
