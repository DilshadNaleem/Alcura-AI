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
            System.out.println("Number of records fetched: " + profit.size());
            System.out.println("First 5 records: " + profit.stream().limit(5).toList());
            model.addAttribute("profit", profit);
            return "/Admin/Profit/ProfitCalculation";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading profit data");
            return "/Admin/Profit/ProfitCalculation";
        }
    }
}