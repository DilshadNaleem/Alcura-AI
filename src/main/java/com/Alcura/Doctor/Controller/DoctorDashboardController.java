package com.Alcura.Doctor.Controller;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DoctorDashboardController
{
    private final DoctorRepository doctorRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private AppointmentRepo appointmentRepo;

    Logger logger = LoggerFactory.getLogger(DoctorDashboardController.class);

    public DoctorDashboardController(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;

    }

    @GetMapping("/Doctor/Dashboard")
    public String showDoctorName(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/Doctor/Signing";
        }

        Doctor doctor = doctorRepository.findByEmailAndStatus(email, 1);

        if (doctor == null) {
            return "redirect:/Doctor/Signing";
        }


        Boolean firstLoginShown = (Boolean) session.getAttribute("firstLoginShown");

        if (doctor.isFirstLogin() && (firstLoginShown == null || !firstLoginShown)) {
            model.addAttribute("showFirstLoginModal", true);
            session.setAttribute("firstLoginShown", true);

            // Update the database
            doctor.setFirstLogin(false);
            doctorRepository.save(doctor);
            logger.info("First login detected and updated for doctor ID: {}", doctor.getId());
        } else {
            model.addAttribute("showFirstLoginModal", false);
        }

        model.addAttribute("adminName", doctor.getFirstName());
        model.addAttribute("adminLastName", doctor.getLastName());
        model.addAttribute("adminImage", doctor.getImage());
        System.out.println("Image Path: " + doctor.getImage());

        try {
            Doctor doctorr = doctorRepository.findByemail(email);
            if (doctorr == null) {
                logger.info("Doctor is null");
            }

            String uniqueId = doctorr.getUniqueId();
            if (uniqueId == null) {
                logger.info("uniqueId is null");
            }

            logger.info("Received uniqueId : {}", uniqueId);

            // Your existing appointment data fetching
            List<Object[]> results = appointmentRepo.findPendingAppointmentsWithCustomerImagesByDoctorEmail(email);
            List<Appoinment> appointments = new ArrayList<>();
            List<String> customerImages = new ArrayList<>();

            for (Object[] result : results) {
                appointments.add((Appoinment) result[0]);
                customerImages.add((String) result[1]);
            }

            if (appointments.isEmpty()) {
                logger.info("No appointments found");
            }

            model.addAttribute("appointments", appointments);
            model.addAttribute("customerImages", customerImages);

            // Add your sales data processing (MODIFIED CODE)
            List<Object[]> sales = appointmentRepo.findCompletedAppointmentsWithPrice(email, doctor.getUniqueId());
            logger.info("Received Sales : {}", sales.size());

            // Create a list to hold price information
            List<Double> prices = new ArrayList<>();
            for (Object[] sale : sales) {
                logger.debug("Sales Array Contents : {}", Arrays.toString(sale));
                if (sale != null && sale.length > 0 && sale[0] != null) {
                    try {
                        // Handle both String and Number cases
                        double priceValue = sale[0] instanceof Number ?
                                ((Number)sale[0]).doubleValue() :
                                Double.parseDouble(sale[0].toString());
                        prices.add(priceValue);
                    } catch (NumberFormatException e) {
                        logger.warn("Could not parse price value: {}", sale[0]);
                        prices.add(0.0); // default value if parsing fails
                    }
                }
            }

            // Add prices to model
            model.addAttribute("prices", prices);
            logger.info("Added {} prices to model", prices.size());

            // Calculate total earnings
            double totalEarnings = prices.stream().mapToDouble(Double::doubleValue).sum();
            model.addAttribute("totalEarnings", totalEarnings);
            logger.info("Total Earnings :{}", totalEarnings);

        } catch (Exception e) {
            logger.error("Error fetching appointments: {}", e.getMessage());
            e.printStackTrace();
        }

        return "/Doctor/Dashboard";
    }



    @GetMapping("/Doctor/Image")
    public ResponseEntity<byte[]> getDoctorImage (HttpSession session)
    {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.notFound().build();
        }

        Doctor doctor = doctorRepository.findByEmailAndStatus(email, 1);
        if (doctor == null || doctor.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(doctor.getImage(), headers, HttpStatus.OK);
    }


}
