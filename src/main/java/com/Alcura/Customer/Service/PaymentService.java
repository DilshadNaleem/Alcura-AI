package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Payment;
import com.Alcura.Customer.Service.Interfaces.PaymentStrategy;
import com.Alcura.Customer.Service.PaymentConcreteStrategies.CreditCardPayment;
import com.Alcura.Customer.Service.PaymentConcreteStrategies.PayPalPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService
{
    private Logger logger = LoggerFactory.getLogger(PaymentService.class);
    public Payment processPayment(Payment payment, String paymentMethod)
    {
        PaymentStrategy strategy = getPaymentStratergy(paymentMethod);
        payment.setPaymentStrategy(strategy);


        String result = payment.processPayment();
        logger.info("Payment Proceed: {}", result);
        return payment;
    }

    private PaymentStrategy getPaymentStratergy(String paymentMethod)
    {
        switch (paymentMethod.toLowerCase())
        {
            case"credit card":
                return new CreditCardPayment();
            case "paypal":
                return new PayPalPayment();
            default:
                throw new IllegalArgumentException("Unknown Payment Method: " + paymentMethod);
        }
    }
}
