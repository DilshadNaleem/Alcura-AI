package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;


@Controller
@RequestMapping("/Admin")
public class AdminEditController
{
    private final AdminRepository adminRepository;
    private Logger logger = LoggerFactory.getLogger(AdminEditController.class);

    public AdminEditController(AdminRepository adminRepository)
    {
        this.adminRepository = adminRepository;
    }

    @GetMapping("/EditProfile")
    public String showEditForm(Model model,
                               HttpSession session)
    {
        String email = (String) session.getAttribute("email");
        if (email == null)
        {
            logger.info("Redriecting to Signing");
            return "redirect:/Admin/Signing";
        }

        Admin admin = adminRepository.findByEmail(email);
        if (admin == null)
        {
            return "redirect:/Admin/Signing";
        }

        model.addAttribute("admin", admin);
        return "/Admin/EditProfile.html";
    }

    @PostMapping("/Editform")
    public void updateAdmin(@RequestParam("firstName")String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam(value="image",required = false)MultipartFile file,
                            HttpSession session,
                            HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try
        {
            String email = (String) session.getAttribute("email");

            if (email == null || email.isEmpty())
            {
                out.println("<script>alert('Session expired!'); window.location.href = '/Admin/Signing';</script>");
                return;
            }

            Admin existingAdmin = adminRepository.findByEmail(email);
            if (existingAdmin == null)
            {
                out.println("<script>alert('Admin not found!'); window.location.href = '/Admin/Signing';</script>");
                return;
            }


            logger.info("Updating Database");
            existingAdmin.setFirstName(firstName);
            existingAdmin.setLastName(lastName);

            if (file != null && !file.isEmpty())
            {
                existingAdmin.setImage(file.getBytes());
            }

            adminRepository.save(existingAdmin);
            out.println("<script>alert('Update Successfully!'); window.location.href = '/Admin/Dashboard';</script>");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error {}" + e.getMessage());
            out.println("<script>alert('Error Updating profile'); window.location.href='/Admin/EditProfile.html';</script>");
        }
    }

    @GetMapping(value = "/image/{adminId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage (@PathVariable String adminId)
    {
        Admin admin = adminRepository.findByEmail(adminId);
        if (admin != null && admin.getImage() != null)
        {
            return admin.getImage();
        }
        return new byte[0];
    }
}
