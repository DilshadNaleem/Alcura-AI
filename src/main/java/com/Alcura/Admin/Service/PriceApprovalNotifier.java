package com.Alcura.Admin.Service;

import com.Alcura.Admin.Service.Interfaces.PriceApprovalObserver;
import com.Alcura.Admin.Service.Interfaces.PriceApprovalSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceApprovalNotifier implements PriceApprovalSubject {
    private static final Logger logger = LoggerFactory.getLogger(PriceApprovalNotifier.class);
    private final List<PriceApprovalObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(PriceApprovalObserver observer) {
        logger.info("Registering new observer: {}", observer.getClass().getSimpleName());
        observers.add(observer);
    }

    @Override
    public void removeObserver(PriceApprovalObserver observer) {
        logger.info("Removing observer: {}", observer.getClass().getSimpleName());
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String email, String subject, String body) {
        logger.info("Notifying {} observers about price approval for email: {}", observers.size(), email);
        logger.debug("Email details - Subject: {}, Body: {}", subject, body);

        if (observers.isEmpty()) {
            logger.error("No observers registered! Email will not be sent.");
            return;
        }

        for (PriceApprovalObserver observer : observers) {
            try {
                logger.debug("Notifying observer: {}", observer.getClass().getSimpleName());
                observer.update(email, subject, body);
                logger.info("Notification sent successfully via {}", observer.getClass().getSimpleName());
            } catch (Exception e) {
                logger.error("Error notifying observer {} for email {}",
                        observer.getClass().getSimpleName(), email, e);
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}