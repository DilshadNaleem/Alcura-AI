package com.Alcura.Admin.Service;

import com.Alcura.Admin.DTO.ProfitCalculationDTO;
import com.Alcura.Customer.Repository.AppointmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfitCalculationService
{
    @Autowired
    AppointmentRepo repository;
    Logger logger = LoggerFactory.getLogger(ProfitCalculationService.class);

    public List<ProfitCalculationDTO> getAppointmentsWithDoctorInfo() {
        List<ProfitCalculationDTO> result = repository.findAllAppointmentsWithDoctorInfo();
        logger.info("Retrieved {} appointments with doctor info", result.size());

        // Detailed logging if needed
        if (logger.isDebugEnabled()) {
            result.forEach(dto -> logger.debug("Appointment: {}", dto));
        }

        return result;
    }
}
