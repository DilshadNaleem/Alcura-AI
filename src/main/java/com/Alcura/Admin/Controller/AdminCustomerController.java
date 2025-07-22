package com.Alcura.Admin.Controller;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Admin")
public class AdminCustomerController
{
    @Autowired
    private CustomerService customerService;

    @GetMapping("/ManageCustomers")
    public String viewAllCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "/Admin/customers";
    }
}
