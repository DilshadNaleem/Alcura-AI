package com.Alcura.Admin.Service.Interfaces;

public interface PriceApprovalSubject
{
    void registerObserver(PriceApprovalObserver observer);
    void removeObserver(PriceApprovalObserver observer);
    void notifyObservers(String email, String subject,String body);
}
