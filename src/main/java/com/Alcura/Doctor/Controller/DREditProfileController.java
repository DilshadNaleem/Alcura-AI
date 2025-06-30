package com.Alcura.Doctor.Controller;

import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.DoctorViewService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/Doctor")
public class DREditProfileController {

    private final DoctorRepository doctorRepository;
    private final DoctorViewService doctorService;
    private Logger logger = LoggerFactory.getLogger(DREditProfileController.class);

    @Autowired
    public DREditProfileController(DoctorRepository doctorRepository,
                                   DoctorViewService doctorService) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    @GetMapping("/Edit")
    public String showEditForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            logger.info("Redirecting to Signing");
            return "redirect:/Doctor/Signing";
        }

        Doctor doctor = doctorRepository.findByemail(email);
        if (doctor == null) {
            return "redirect:/Doctor/Signing";
        }

        model.addAttribute("doctor", doctor);
        return "/Doctor/EditProfile";
    }

    @PostMapping("/Editform")
    public void updateDoctor(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("specialist") String specialist,
                             @RequestParam("contactNumber") String contact,
                             @RequestParam("specialist_info") String specialist_info,
                             @RequestParam("Qualification") String qualification,
                             @RequestParam("government_Hospitals") String government_Hos,
                             @RequestParam(value = "image", required = false) MultipartFile imageFile,
                             HttpSession session,
                             HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                out.println("<script>alert('Session expired!'); window.location.href = '/Doctor/Signing';</script>");
                return;
            }

            Doctor existingDoctor = doctorRepository.findByemail(email);
            if (existingDoctor == null) {
                out.println("<script>alert('Doctor not found!'); window.location.href = '/Doctor/Signing';</script>");
                return;
            }

            // Update fields
            logger.info("Updating fields");
            existingDoctor.setFirstName(firstName);
            existingDoctor.setLastName(lastName);
            existingDoctor.setSpecialist(specialist);
            existingDoctor.setContactNumber(contact);
            existingDoctor.setSpecialist_info(specialist_info);
            existingDoctor.setQualification(qualification);
            existingDoctor.setGovernment_Hospitals(government_Hos);

            if (imageFile != null && !imageFile.isEmpty()) {
                existingDoctor.setImage(imageFile.getBytes());
            }

            doctorRepository.save(existingDoctor);

            out.println("<script>alert('Update Successfully!'); window.location.href = '/Doctor/Dashboard';</script>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('Error Updating profile'); window.location.href='/Doctor/EditProfile';</script>");
        }
    }

    @GetMapping(value = "/image/{doctorId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable String doctorId) {
        Doctor doctor = doctorRepository.findByemail(doctorId);
        if (doctor != null && doctor.getImage() != null) {
            return doctor.getImage();
        }
        return new byte[0];
    }
}