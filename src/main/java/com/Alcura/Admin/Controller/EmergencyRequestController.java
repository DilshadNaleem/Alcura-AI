package com.Alcura.Admin.Controller;

import com.Alcura.Customer.DTO.EmergencyRequest;
import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.EmergencyRequestRepository;
import com.Alcura.Customer.Repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Admin")
public class EmergencyRequestController {

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final HospitalRepository hospitalRepository;

    @Value("${ors.api.key}")
    private String apiKey;

    public EmergencyRequestController(EmergencyRequestRepository emergencyRequestRepository,
                                      HospitalRepository hospitalRepository) {
        this.emergencyRequestRepository = emergencyRequestRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @GetMapping("/SOSView")
    public String getAllEmergencyRequests(Model model) {
        List<EmergencyRequest> requests = emergencyRequestRepository.findAll();
        addHospitalDataToModel(requests, model);
        model.addAttribute("orsApiKey", apiKey);
        return "/Admin/SOSView";
    }

    @GetMapping("/SOSView/active")
    public String getActiveEmergencyRequests(Model model) {
        List<EmergencyRequest> activeRequests = emergencyRequestRepository.findByStatus("ACTIVE");
        addHospitalDataToModel(activeRequests, model);
        model.addAttribute("statusFilter", "ACTIVE");
        model.addAttribute("orsApiKey", apiKey);
        return "/Admin/SOSView";
    }

    @GetMapping("/SOSView/completed")
    public String getCompletedEmergencyRequests(Model model) {
        List<EmergencyRequest> completedRequests = emergencyRequestRepository.findByStatus("COMPLETED");
        addHospitalDataToModel(completedRequests, model);
        model.addAttribute("statusFilter", "COMPLETED");
        model.addAttribute("orsApiKey", apiKey);
        return "/Admin/SOSView";
    }

    @GetMapping("/SOSView/cancelled")
    public String getCancelledEmergencyRequests(Model model) {
        List<EmergencyRequest> cancelledRequests = emergencyRequestRepository.findByStatus("CANCELLED");
        addHospitalDataToModel(cancelledRequests, model);
        model.addAttribute("statusFilter", "CANCELLED");
        model.addAttribute("orsApiKey", apiKey);
        return "/Admin/SOSView";
    }

    private void addHospitalDataToModel(List<EmergencyRequest> requests, Model model) {
        requests.forEach(request -> {
            if (request.getHospitalId() != null) {
                Optional<Hospital> hospital = hospitalRepository.findById(request.getHospitalId());
                hospital.ifPresent(h -> {
                    request.setHospitalLatitude(h.getLatitude());
                    request.setHospitalLongitude(h.getLongitude());
                    request.setHospitalName(h.getName());
                });
            }
        });
        model.addAttribute("requests", requests);
    }
}