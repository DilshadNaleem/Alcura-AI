package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Doctor.Service.DoctorPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Admin")
public class DoctorPriceController
{
    @Autowired
    private DoctorPriceService doctorPriceService;

    @GetMapping("/Manage_Doctor/Price")
    public String viewDoctorPrice(Model model)
    {
        List<DoctorPrice> doctorPrice = doctorPriceService.getAllPriceList();
        model.addAttribute("doctorPrices", doctorPrice);
        return "/Admin/Doctor/DoctorManagePrice";
    }

}
