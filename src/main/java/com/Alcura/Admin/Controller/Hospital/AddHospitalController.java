package com.Alcura.Admin.Controller.Hospital;


import com.Alcura.Admin.Service.AddHospitalService;
import com.Alcura.Customer.Model.Hospital;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@Controller
@RequestMapping("/Admin")
public class AddHospitalController {
    private static final Logger logger = LoggerFactory.getLogger(AddHospitalController.class);
    private final AddHospitalService hospitalService;

    @Autowired
    public AddHospitalController(AddHospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/AddNewHospital")
    public String addhospitalform() {
        logger.info("Accessed Add Hospital form");
        return "/Admin/Hospital/Add_Hospital";
    }

    @PostMapping("/AddHospital")
    public void addHospital(
            @RequestParam("hospitalName") String name,
            @RequestParam("latitude") BigDecimal latitude,
            @RequestParam("longitude") BigDecimal longitude,
            @RequestParam(value = "email", required = false) String contactEmail,
            @RequestParam("address") String address,
            @RequestParam(value = "phone", required = false) String phoneNumber,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "images", required = false) MultipartFile image,
            HttpServletResponse response) throws IOException {

        logger.debug("Attempting to register new hospital with name: {}", name);

        // Set response content type
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        try {

            if (contactEmail != null && !contactEmail.isEmpty()) {
                if (hospitalService.emailExists(contactEmail))
                {
                    writer.println("<script type='text/javascript'>");
                    writer.println("alert('Error: This email is already registered');");
                    writer.println("window.location.href='/Admin/AddNewHospital';");
                    writer.println("</script>");
                    return;
                }
            }

            if (phoneNumber != null && ! phoneNumber.isEmpty())
            {
                if (hospitalService.phoneNumberExists(phoneNumber))
                {
                    writer.println("<script type='text/javascript'>");
                    writer.println("alert('Error: This number is already registered');");
                    writer.println("window.location.href='/Admin/AddNewHospital';");
                    writer.println("</script>");
                    return;
                }
            }

            if (image != null) {
                logger.debug("Received {} image files for hospital {}", image, name);
            } else {
                logger.warn("No images received for hospital {}", name);
            }

            Hospital hospital = hospitalService.registerHospital(
                    name, latitude, longitude, contactEmail,
                    address, phoneNumber, description, image);

            logger.info("Successfully registered hospital: {} with ID: {}", name, hospital.getUniqueId());

            // Write JavaScript to show success alert and redirect
            writer.println("<script type='text/javascript'>");
            writer.println("alert('Hospital registered successfully with ID: " + hospital.getUniqueId() + "');");
            writer.println("window.location.href='/Admin/AddNewHospital';");
            writer.println("</script>");

        } catch (IllegalArgumentException e) {
            logger.error("Validation error while registering hospital {}: {}", name, e.getMessage());

            writer.println("<script type='text/javascript'>");
            writer.println("alert('Invalid data: " + e.getMessage().replace("'", "\\'") + "');");
            writer.println("window.location.href='/Admin/AddNewHospital';");
            writer.println("</script>");

        } catch (Exception e) {
            logger.error("Unexpected error while registering hospital {}: {}", name, e.getMessage(), e);

            writer.println("<script type='text/javascript'>");
            writer.println("alert('Failed to register hospital: " + e.getMessage().replace("'", "\\'") + "');");
            writer.println("window.location.href='/Admin/AddNewHospital';");
            writer.println("</script>");
        }
    }
}