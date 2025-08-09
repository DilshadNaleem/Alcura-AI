package com.Alcura.FactoryPattern.Service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public interface LoginHandler {
    void handleLogin(HttpServletResponse response, HttpSession session) throws Exception;
}
