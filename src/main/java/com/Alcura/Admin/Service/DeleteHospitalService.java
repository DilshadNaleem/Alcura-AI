package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;

@Service
public class DeleteHospitalService
{
    private final Logger logger = LoggerFactory.getLogger(DeleteHospitalService.class);
    private final HospitalRepository hospitalRepository;

    public DeleteHospitalService(HospitalRepository hospitalRepository)
    {
        this.hospitalRepository = hospitalRepository;
    }


    @Transactional
    public void deleteHospitalByUniqueId(String uniqueId, PrintWriter out)
    {
        try
        {
            Hospital hospital = hospitalRepository.findByUniqueId(uniqueId);

            if (hospital != null)
            {
                hospitalRepository.delete(hospital);
                out.println("<script type = 'text/javascript'>");
                out.println("alert('Hospital Deleted Successfully');");
                out.println("window.location.href = '/Admin/ManageHospitals'");
                out.println("</script>");
            }

            else
            {
                out.println("<script type='text/javascript'>");
                out.println("alert('Hospital not found');");
                out.println("window.location.href = '/Admin/ManageHospitals'");
                out.println("</script>");
            }
        }

        catch (Exception e)
        {
            e.getMessage();
            e.printStackTrace();
            logger.error("Error deleting hospital with id {}: {}", uniqueId, e.getMessage());
            out.println("<script type= 'text/javascript'>");
            out.println("alert ('Error deleting hospital " + e.getMessage().replace("'", "\\'") + "');");
            out.println("window.history.back();");
            out.println("</script>");
        }
    }
}
