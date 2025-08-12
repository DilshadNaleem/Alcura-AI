package com.Alcura.Customer.Service.Interfaces;

public interface PaymentStrategy
{
    String processPayment(float amount);
    String getPaymentMethodName();
}
