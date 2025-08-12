package com.Alcura.Customer.Service.PaymentConcreteStrategies;

import com.Alcura.Customer.Service.Interfaces.PaymentStrategy;

public class PaymentContext
{
    private PaymentStrategy paymentStrategy;

    public PaymentContext(PaymentStrategy paymentStrategy)
    {
        this.paymentStrategy = paymentStrategy;
    }

    public String executePayment(float amount)
    {
        return paymentStrategy.processPayment(amount);
    }

    public String getPaymentMethodName()
    {
        return paymentStrategy.getPaymentMethodName();
    }
}
