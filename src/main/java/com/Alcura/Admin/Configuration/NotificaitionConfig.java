package com.Alcura.Admin.Configuration;

import com.Alcura.Admin.Service.EmailNotificaitonService;
import com.Alcura.Admin.Service.PriceApprovalNotifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificaitionConfig
{

    @Bean
    public PriceApprovalNotifier priceApprovalNotifier(EmailNotificaitonService emailService) {
        PriceApprovalNotifier notifier = new PriceApprovalNotifier();
        notifier.registerObserver(emailService);  // Register the observer
        return notifier;
    }
}
