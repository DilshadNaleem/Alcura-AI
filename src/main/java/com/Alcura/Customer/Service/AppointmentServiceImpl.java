package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppoinmentService;
import com.Alcura.Customer.Service.Interfaces.AppointmentEmailService;
import com.Alcura.Customer.Service.Interfaces.AppointmentRepo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentServiceImpl implements AppoinmentService {
    private final AppointmentId appointmentId;
    private final AppointmentEmailService appointmentEmailService;
    private final AppointmentRepo appointmentRepo;
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);


    public AppointmentServiceImpl(AppointmentRepo appointmentRepo,
                                  AppointmentId appointmentId,
                                  AppointmentEmailService appointmentEmailService)
    {
        this.appointmentEmailService = appointmentEmailService;
        this.appointmentId = appointmentId;
        this.appointmentRepo = appointmentRepo;
    }
    @Override
    public Appoinment createAppointment(
            String doctorId,
            String doctorName,
            String time,
            LocalDate date,
            String specialReason,
            String customerEmail
    )

    {
        logger.info("Starting to create appointment for doctor: {} with customer email: {}", doctorName, customerEmail);

        Appoinment appointment = new Appoinment();
        appointment.setDoctor(doctorId);
        appointment.setDoctor_name(doctorName);
        appointment.setAppointment_date(date);
        appointment.setAppointment_time(time);
        appointment.setSpecial_reasons(specialReason);
        appointment.setStatus("Pending");
        appointment.setCustomer_email(customerEmail);

        logger.debug("Appointment object created with details - Doctor: {}, Date: {}, Reason: {}",
                doctorName, date, specialReason);

        // Now generate the unique ID
        String uniqueId = appointmentId.createAppointment();
        appointment.setUnique_id(uniqueId);  // Ensure this doesn't return null

        if (uniqueId == null) {
            logger.error("Failed to generate unique ID for appointment");
            throw new IllegalStateException("Could not generate appointment ID");
        }

        logger.debug("Generated unique appointment ID: {}", uniqueId);

        try {
            Appoinment savedAppointment = appointmentRepo.save(appointment);
            logger.info("Appointment successfully saved with ID: {}", savedAppointment.getUnique_id());

            appointmentEmailService.sendAppointmentConfirmaation(customerEmail, date,doctorName, uniqueId,time);
            logger.info("Confirmation email sent to: {}", customerEmail);

            return savedAppointment;
        } catch (Exception e) {
            logger.error("Failed to save appointment or send confirmation email", e);
            throw e;
        }
    }
}
