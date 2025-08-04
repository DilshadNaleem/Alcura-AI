package com.Alcura.Admin.Controller.Profit;

import com.Alcura.Admin.DTO.ProfitCalculationDTO;
import com.Alcura.Admin.Service.ProfitCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/Admin")
@Controller
public class ProfitCalculationController {

    private final ProfitCalculationService profitCalculationService;

    @Autowired
    public ProfitCalculationController(ProfitCalculationService profitCalculationService) {
        this.profitCalculationService = profitCalculationService;
    }

    @GetMapping("/ProfitCalculation")
    public String form(Model model) {
        try {
            List<ProfitCalculationDTO> profit = profitCalculationService.getAppointmentsWithDoctorInfo();
            model.addAttribute("profit", profit);
            return "/Admin/Profit/ProfitCalculation";
        } catch (Exception e) {
            e.printStackTrace();
            // Consider using the logger from the service class here as well
            return "/Admin/Profit/ProfitCalculation";
        }
    }
}