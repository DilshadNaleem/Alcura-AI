package com.Alcura.FactoryPattern.ServiceFactory;

import com.Alcura.Admin.DTO.AdminRegisterRequest;
import com.Alcura.Admin.Service.Interfaces.AdminAuthService;
import com.Alcura.Customer.DTO.RegisterRequest;
import com.Alcura.Customer.Service.Interfaces.CustomerAuthService;
import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.Doctor.Service.Interfaces.DoctorAuthService;
import com.Alcura.FactoryPattern.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationServiceFactory
{
 private final AdminAuthService adminAuthService;
 private final CustomerAuthService customerAuthService;
 private final DoctorAuthService doctorAuthService;

 @Autowired
    public RegistrationServiceFactory(AdminAuthService adminAuthService,
                                      CustomerAuthService customerAuthService,
                                      DoctorAuthService doctorAuthService)
 {
     this.adminAuthService = adminAuthService;
     this.doctorAuthService = doctorAuthService;
     this.customerAuthService = customerAuthService;
 }

 public RegistrationService getService(String userType)
 {
     switch (userType.toLowerCase())
     {
         case "admin" :
             return (request, session) -> adminAuthService.registerAdmin((AdminRegisterRequest) request, session);
         case "customer" :
             return (request, session) -> customerAuthService.registerCustomer((RegisterRequest) request,session);
         case "doctor" :
             return (request, session) -> doctorAuthService.registerDoctor((DoctorRegisterRequest) request, session);

         default:
             throw new IllegalArgumentException("Unknown user Type: " + userType);
     }
 }
}
