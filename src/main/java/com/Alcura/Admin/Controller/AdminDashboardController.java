package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.ProfitCalculationDTO;
import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Repository.AppointmentRepository;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class AdminDashboardController {

    private final AdminRepository adminRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);
    private final DoctorRepository doctorRepository;
    private final AppointmentRepo appointmentRepo;

    public AdminDashboardController(AdminRepository adminRepository,
                                    AppointmentRepository appointmentRepository,
                                    CustomerRepository customerRepository,
                                    DoctorRepository doctorRepository,
                                    AppointmentRepo appointmentRepo)
    {
        this.adminRepository = adminRepository;
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepo = appointmentRepo;
    }


    @GetMapping("/Admin/Dashboard")
    public String showAdminDashboard(Model model, HttpSession session) {
        // 1. Check admin authentication (from showAdminName)
        String email = (String) session.getAttribute("email");
        if (email == null || email.isEmpty()) {
            return "redirect:/Admin/Signing";
        }

        Admin admin = adminRepository.findByEmailAndStatus(email, 1);
        if (admin == null) {
            return "redirect:/Admin/Signing";
        }

        // 2. Add admin details to model
        model.addAttribute("adminName", admin.getFirstName());
        model.addAttribute("adminImage", admin.getImage());
        System.out.println("Image Path: " + admin.getImage());

        try {
            // 3. Load recent entities (from showAdminName)
            List<Appoinment> appointments = appointmentRepository.findRecentAppointments();
            List<Customer> customers = customerRepository.findRecentCustomers();
            List<Doctor> doctors = doctorRepository.findRecentDoctors();

            model.addAttribute("appointments", appointments);
            model.addAttribute("customers", customers);
            model.addAttribute("doctors", doctors);

            // 4. Add sales/profit calculations (from profit())
            List<ProfitCalculationDTO> allAppointments = appointmentRepo.findAllAppointmentsWithDoctorInfo();

            double totalSales = 0.0;
            double totalProfit = 0.0;
            double totalExpenses = 0.0;
            double hospital = 0.0;
            int totalAppointments = allAppointments.size();

            for (ProfitCalculationDTO appointment : allAppointments) {
                Float price = appointment.getPrice();
                Float newPrice = appointment.getNewPrice();
                Double hospitalPrice = appointment.getHospital_price();
                hospital = hospitalPrice;
                if (price != null) {
                    totalSales +=  newPrice;
                    if (newPrice != null) {
                        totalProfit += (  newPrice - price);
                        totalExpenses += price;
                    }
                }
            }

            model.addAttribute("totalSales", totalSales);
            model.addAttribute("totalProfit", totalProfit);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("totalAppointments", totalAppointments);
            model.addAttribute("profitPercentage", hospital);

        } catch (Exception e) {
            logger.error("Error loading dashboard data: {}", e.getMessage());
            // Initialize empty attributes to prevent Thymeleaf errors
            model.addAttribute("appointments", List.of());
            model.addAttribute("customers", List.of());
            model.addAttribute("doctors", List.of());
            model.addAttribute("totalSales", 0.0);
            model.addAttribute("totalProfit", 0.0);
            model.addAttribute("totalAppointments", 0);
        }

        return "/Admin/AdminDashboard";
    }

    @GetMapping("/Admin/DoctorImage/{email}")
    public void showDoctorImage(@PathVariable String email, HttpServletResponse response) throws IOException
    {
        Doctor doctor = doctorRepository.findByemail(email);
        if (doctor != null && doctor.getImage() != null)
        {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(doctor.getImage());
            response.getOutputStream().close();
        }
    }

    @GetMapping("/Admin/Image")
    public ResponseEntity<byte[]> getDoctorImage (HttpSession session)
    {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.notFound().build();
        }

        Admin admin = adminRepository.findByEmailAndStatus(email, 1);
        if (admin == null || admin.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(admin.getImage(), headers, HttpStatus.OK);
    }

    @GetMapping("/Admin/ViewRecentAppointments")
    public String showAppointments(Model model, HttpSession session)
    {
        try
        {
            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                return "redirect:/Admin/Signing";
            }
            logger.debug("Fetching all appointments from repi... ");
            List<Appoinment> appoinments = appointmentRepository.findAll();
            logger.debug("Found {} appointments " ,appoinments.size());

            model.addAttribute("appointments",appoinments);
            return "/Admin/AdminDashboard";
        }
        catch (Exception e)
        {
            logger.debug("Error {}", e.getMessage());
            e.printStackTrace();
        }
        logger.debug("Returned Null");
    return null;

    }

}