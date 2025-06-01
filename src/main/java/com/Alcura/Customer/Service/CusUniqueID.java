package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Optional;

@Component
public class CusUniqueID
{
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer (Customer customer)
    {
        Optional<Customer> maxCustomer = customerRepository.findTopByOrderByIdDesc();
        String nextUniqueId = generateUniqueId(maxCustomer);
        customer.setUniqueId(nextUniqueId);
        return customerRepository.save(customer);
    }

    private String generateUniqueId(Optional<Customer> maxCustomer)
    {
        String nextUniqueId;
        if(maxCustomer.isPresent())
        {
            int nextId = Integer.parseInt(maxCustomer.get().getUniqueId().split("_")[1]) +1;
            nextUniqueId = "CUS_" + new DecimalFormat("00").format(nextId);
        }
        else
        {
            nextUniqueId = "CUS_01";
        }

        return nextUniqueId;
    }
}
