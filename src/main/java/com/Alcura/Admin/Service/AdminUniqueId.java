package com.Alcura.Admin.Service;

import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Optional;

@Component
public class AdminUniqueId {

    @Autowired
    private AdminRepository adminRepository;

    public Admin createAdmin (Admin admin)
    {
        Optional<Admin> maxAdmin = adminRepository.findTopByOrderByIdDesc();
        String nextUniqueId = generateUniqueId(maxAdmin);
        admin.setUniqueId(nextUniqueId);
        return adminRepository.save(admin);
    }

    private String generateUniqueId(Optional<Admin> maxAdmin)
    {
        String nextUniqueId;
        if (maxAdmin.isPresent())
        {
            int nextId = Integer.parseInt(maxAdmin.get().getUniqueId().split("_")[1]) + 1;
            nextUniqueId = "ADMIN_" + new DecimalFormat("00").format(nextId);
        }
        else
        {
            nextUniqueId = "ADMIN_01";
        }
        return nextUniqueId;
    }
}
