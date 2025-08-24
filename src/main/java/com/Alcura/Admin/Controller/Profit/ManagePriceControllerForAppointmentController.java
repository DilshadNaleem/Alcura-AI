package com.Alcura.Admin.Controller.Profit;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/Admin")
public class ManagePriceControllerForAppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(ManagePriceControllerForAppointmentController.class);

    @Autowired
    private DoctorPriceRepo doctorPriceRepo;

    public ManagePriceControllerForAppointmentController(DoctorPriceRepo doctorPriceRepo)
    {
        this.doctorPriceRepo = doctorPriceRepo;
    }

    @GetMapping("/Manage_Percentage")
    public String form(Model model) {

        List<DoctorPrice> prices = doctorPriceRepo.findAll();

        DoctorPrice latestPrice = doctorPriceRepo.findTopByOrderByIdDesc()
                .orElse(new DoctorPrice());
        model.addAttribute("prices", prices);
        model.addAttribute("price", latestPrice);
        return "/Admin/Profit/SetPercentage";
    }

    @PostMapping("/SetPercentage")
    public String percentage(@RequestParam("range") Double range,
                             HttpSession session,
                             PrintWriter out) {
        try {
            logger.info("Received Data {}", range);

            // Get all existing prices
            List<DoctorPrice> allPrices = doctorPriceRepo.findAll();

            if (allPrices.isEmpty()) {
                // If no records exist, create a new one
                DoctorPrice newPrice = new DoctorPrice();
                newPrice.setHospital_price(range);
                doctorPriceRepo.save(newPrice);
            } else {
                // Update all existing records with the new price
                for (DoctorPrice price : allPrices) {
                    price.setHospital_price(range);
                }
                doctorPriceRepo.saveAll(allPrices);
            }

            logger.info("Updated all records in DB");

            out.println("<script>");
            out.println("alert('Details Saved to Database for all records');");
            out.println("window.location.href ='/Admin/Manage_Percentage';");
            out.println("</script>");

        } catch (Exception e) {
            logger.error("Error: ", e);
            out.println("<script>alert('Error occurred while saving');</script>");
        }
        return null;
    }
}