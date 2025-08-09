package com.Alcura.FactoryPattern.LoginFactory;

import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.AdminLoginAuthImpl;
import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.CusLoginAuthServiceImpl;
import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
import com.Alcura.FactoryPattern.Service.LoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginHandlerFactory
{
    public static LoginHandler createHandler(String role, Object authService, Object request,
                                             HttpServletResponse response, HttpSession session,
                                             Object repository) throws IOException {
        switch (role.toLowerCase()) {
            case "admin":
                return new AdminLoginHandler(
                        (AdminLoginAuthImpl) authService,
                        (AdminLoginRequest) request,
                        response,
                        session
                );
            case "customer":
                return new CustomerLoginHandler(
                        (CusLoginAuthServiceImpl) authService,
                        (LoginRequest) request,
                        response,
                        session
                );
            case "doctor":
                return new DoctorLoginHandler(
                        (DRLoginAuthServiceImpl) authService,
                        (DRLoginRequest) request,
                        (DoctorRepository) repository,
                        response,
                        session
                );
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
