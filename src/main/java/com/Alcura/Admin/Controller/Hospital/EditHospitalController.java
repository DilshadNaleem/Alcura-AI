package com.Alcura.Admin.Controller.Hospital;

import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@Controller
@RequestMapping("/Admin")
public class EditHospitalController
{
    private final HospitalRepository hospitalRepository;
    private Logger logger = LoggerFactory.getLogger(EditHospitalController.class);

    public EditHospitalController(HospitalRepository hospitalRepository)
    {
        this.hospitalRepository = hospitalRepository;
    }

    @GetMapping("/EditHospitals/{id}")
    public String showForm(@PathVariable String id, Model model,
                           HttpSession session)
    {
        String email = (String) session.getAttribute("email");
        if (email == null)
        {
            logger.info("Redirecting to Signing");
            return "redirect:/Admin/Signing";
        }

        Hospital hospital = hospitalRepository.findByUniqueId(id);
        if (hospital == null)
        {
            return "redirect:/Admin/Signing";
        }

        model.addAttribute("hospital", hospital);
        return "/Admin/Hospital/Edit_Hospital";
    }

    @PostMapping("/EditHospital")
    public void updateForm ( @RequestParam("uniqueId") String id,
                             @RequestParam("fullname") String name,
                             @RequestParam("address") String address,
                             @RequestParam("email") String email,
                             @RequestParam("description") String description,
                             @RequestParam("latitude") BigDecimal latitude,
                             @RequestParam("longitude") BigDecimal longitude,
                             @RequestParam("contactNumber") String contact,
                             @RequestParam(value = "image", required = false)MultipartFile file,
                             HttpSession session,
                             HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try
        {
            String sessionemail = (String) session.getAttribute("email");

            if (sessionemail == null || sessionemail.isEmpty())
            {
                out.println("<script>");
                out.println("alert('Session Expired!');");
                out.println("window.location.href = '/Admin/Signing'");
                return ;
            }

            Hospital exisitngHospital = hospitalRepository.findByUniqueId(id);

            if (exisitngHospital == null)
            {
                out.println("<script> alert ('Hospital not found!'); window.location.href='/Admin/ManageHospital';</script>");
                return;
            }

            exisitngHospital.setUniqueId(id);
            exisitngHospital.setName(name);
            exisitngHospital.setAddress(address);
            exisitngHospital.setContactEmail(email);
            exisitngHospital.setDescription(description);
            exisitngHospital.setLatitude(latitude);
            exisitngHospital.setLongitude(longitude);
            exisitngHospital.setPhoneNumber(contact);

            if (file != null && !file.isEmpty())
            {
                exisitngHospital.setImage(file.getBytes());
            }

            hospitalRepository.save(exisitngHospital);
            out.println("<script>alert('Updated Successfully!'); window.location.href= '/Admin/ManageHospitals';</script>");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            out.println("<script> alert('Error Updating Profile'); window.location.href = '/Admin/ManageHospitals';</script>");
        }
    }
}
