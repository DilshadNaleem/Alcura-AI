package com.Alcura.FactoryPattern.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

public interface RegistrationService
{
    ResponseEntity<String> register (Object registerRequest, HttpSession session);
}
