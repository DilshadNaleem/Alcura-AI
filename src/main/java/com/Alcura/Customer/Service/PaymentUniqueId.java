package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Payment;
import com.Alcura.Customer.Repository.PaymentRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Optional;

@Component
public class PaymentUniqueId {
    @Autowired
    private PaymentRepo paymentRepo;
    private Logger logger = LoggerFactory.getLogger(PaymentUniqueId.class);

    public Payment createPayment(Payment payment) {
        try {
            Optional<Payment> maxPayment = paymentRepo.findTopByOrderByIdDesc();
            String nextUniqueId = generateUniqueId(maxPayment);
            payment.setUniqueId(nextUniqueId);
            return paymentRepo.save(payment);
        } catch (Exception e) {
            logger.error("Error creating payment: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    private String generateUniqueId(Optional<Payment> maxPayment) {
        try {
            if (!maxPayment.isPresent() || maxPayment.get().getUniqueId() == null) {
                logger.info("No existing payment found, starting with initial ID");
                return "Payment_01";
            }

            String lastUniqueId = maxPayment.get().getUniqueId();
            String[] parts = lastUniqueId.split("_");

            if (parts.length != 2) {
                logger.warn("Invalid unique ID format found: {}, starting with initial ID", lastUniqueId);
                return "Payment_01";
            }

            int nextId = Integer.parseInt(parts[1]) + 1;
            return "Payment_" + new DecimalFormat("00").format(nextId);
        } catch (NumberFormatException e) {
            logger.error("Error parsing payment ID: {}", e.getMessage());
            return "Payment_01";
        } catch (Exception e) {
            logger.error("Unexpected error generating unique ID: {}", e.getMessage());
            return "Payment_01";
        }
    }
}