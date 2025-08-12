package com.Alcura.Customer.Service.PaymentConcreteStrategies;

import com.Alcura.Customer.Service.Interfaces.PaymentStrategy;

public class PayPalPayment implements PaymentStrategy
{
    @Override
    public String processPayment(float amount) {
        return "PayPal payment processed for amount: " + amount;
    }

    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
}
