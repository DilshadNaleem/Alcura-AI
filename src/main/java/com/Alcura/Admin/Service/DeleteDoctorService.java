package com.Alcura.Admin.Service;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.io.PrintWriter;

@Service
public class DeleteDoctorService
{
    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public void deleteDoctorByUniqueId(String uniqueId, PrintWriter out)
    {
        try
        {
            Doctor doctor = doctorRepository.findByuniqueId(uniqueId);

            if (doctor != null)
            {
                doctorRepository.delete(doctor);
                out.println("<script>  alert ('Doctor Deleted Successfully!'); window.location.href = '/Admin/Manage_Doctors'; </script>" );
            }
            else
            {
                out.println("<script> alert ('Doctor Not found!'); window.locaiton.href = '/Admin/Manage_Doctors'; </script>");

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
            out.println("<script> alert ('Error Deleting Doctor'); window.history.back(); </script>");
        }
    }

}


