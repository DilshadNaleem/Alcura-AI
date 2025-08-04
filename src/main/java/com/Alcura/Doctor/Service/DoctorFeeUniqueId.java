package com.Alcura.Doctor.Service;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Optional;

@Service
public class DoctorFeeUniqueId
{
    @Autowired
    private DoctorPriceRepo doctorPriceRepo;
    private Logger logger = LoggerFactory.getLogger(DoctorFeeUniqueId.class);


    public DoctorPrice createDoctorPrice (DoctorPrice doctorPrice)
    {
        try {
            Optional<DoctorPrice> maxPrice = doctorPriceRepo.findFirstByOrderByIdDesc();
            String nextUniqueId = generateUniqueId(maxPrice);
            doctorPrice.setUnique_id(nextUniqueId);
            return doctorPriceRepo.save(doctorPrice);
        }
        catch (Exception e)
        {
            logger.error("Error Creating Id: {}", e.getMessage());
            throw new RuntimeException("Failed to create ", e);
        }
    }


    private String generateUniqueId(Optional<DoctorPrice> maxPrice)
    {
        try
        {
            if(!maxPrice.isPresent() || maxPrice.get().getUnique_id() == null)
            {
                logger.info("No Exisitng payment found, Starting with initial");
                return "DoctorFee_01";
            }

            String lastUniqueId = maxPrice.get().getUnique_id();
            String[] parts = lastUniqueId.split("_");

            if (parts.length != 2)
            {
                logger.warn("Inavlid Id Format: {}", lastUniqueId);
                return "DoctorFee_01";
            }

            int nextId = Integer.parseInt(parts[1]) +1;
            return "DoctorFee_" + new DecimalFormat("00").format(nextId);
        }
        catch (NumberFormatException e)
        {
            logger.error("Error parsing payment Id: {}", e.getMessage());
            return "DoctorFee_01";
        }

        catch (Exception e)
        {
            logger.error("Unexpected error : {}", e.getMessage() );
            return "DoctorFee_01";
        }
    }
}
