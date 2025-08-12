package com.Alcura.Customer.Service.PaymentConcreteStrategies;

import com.Alcura.Customer.Service.Interfaces.PaymentStrategy;

public class CreditCardPayment implements PaymentStrategy
{
    @Override
    public String getPaymentMethodName() {
        return "Credit Card ";
    }

    @Override
    public String processPayment(float amount) {
        return "Credit Card payment processed for amount: " + amount;
    }
}
