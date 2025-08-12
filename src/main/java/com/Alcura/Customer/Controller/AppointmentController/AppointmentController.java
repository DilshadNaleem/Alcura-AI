package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Model.Payment;
import com.Alcura.Customer.Repository.AppointmentRepository;
import com.Alcura.Customer.Repository.PaymentRepo;
import com.Alcura.Customer.Service.DoctorAvailabilityService;
import com.Alcura.Customer.Service.Interfaces.AppoinmentService;
import com.Alcura.Customer.Service.PaymentService;
import com.Alcura.Customer.Service.PaymentUniqueId;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;

@RestController
public class AppointmentController {
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppoinmentService appointmentService;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PaymentRepo paymentRepo;
    private PaymentService paymentService;
    private final DoctorAvailabilityService doctorAvailabilityService;
    private final PaymentUniqueId paymentUniqueId;

    public AppointmentController(AppoinmentService appointmentService,
                                 DoctorAvailabilityService doctorAvailabilityService,
                                 PaymentUniqueId paymentUniqueId,
                                 PaymentService paymentService) {
        this.appointmentService = appointmentService;
        this.doctorAvailabilityService = doctorAvailabilityService;
        this.paymentUniqueId = paymentUniqueId;
        this.paymentService = paymentService;
    }

    @PostMapping("/Customer/Appointment")
    public void createAppointment(
            @RequestParam("doctorId") String doctorId,
            @RequestParam("doctorName") String doctorName,
            @RequestParam("appointment_date") LocalDate date,
            @RequestParam("appointment_time") String time,
            @RequestParam(value = "specialReasons", required = false) String specialReason,
            @RequestParam("appointmentPrice") Float price,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpSession session,
            HttpServletResponse response) throws IOException {

        logger.info("Recieved files {}",  paymentMethod );
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        try {
            String email = (String) session.getAttribute("email");
            logger.info("Sessioned Email: {}", email);
            logger.info("Doctor name: {} {}", doctorName, doctorId);

            if (email == null) {
                sendAlert(writer, "You need to login first!", "/Customer/Signing");
                return;
            }

            // Check if slot is already booked
            if (appointmentRepository.existsByDoctorAndDateAndTime(doctorId, date, time)) {
                sendAlert(writer, "This time slot is already booked! Please choose another time.", "/Customer/AppointmentBooking");
                return;
            }


            Appoinment appoinment = new Appoinment();


            Payment payment = new Payment();
            payment.setPrice(price);
            paymentService.processPayment(payment,paymentMethod);
            payment.setCustomer(email);
            payment = paymentUniqueId.createPayment(payment);



            Appoinment appointment = appointmentService.createAppointment(
                    doctorId,
                    doctorName,
                    time,
                    date,
                    specialReason,
                    email,
                    price,
                    paymentMethod
            );

            appointment.setPaymentId(payment.getUniqueId());
            payment.setAppointment(appointment.getUnique_id());

            paymentRepo.save(payment);

            sendAlert(writer, "Appointment created successfully!", "/Customer/AppointmentBooking");

        } catch (Exception e) {
            logger.error("Error creating appointment: ", e);
            sendAlert(writer, "Error creating appointment: " + e.getMessage(),
                    "/Customer/AppointmentBooking");
        } finally {
            writer.close();
        }
    }

    private void sendAlert(PrintWriter writer, String message, String redirectUrl) {
        writer.println("<script type='text/javascript'>");
        writer.println("alert('" + message.replace("'", "\\'") + "');");
        writer.println("window.location='" + redirectUrl + "';");
        writer.println("</script>");
    }
}