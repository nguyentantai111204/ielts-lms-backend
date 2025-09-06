package com.ntt.lms.service;

import com.ntt.lms.pojo.ConsultRequest;
import com.ntt.lms.repository.ConsultRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultRequestService {

    private final ConsultRequestRepository consultRequestRepository;
    private final NotificationsService notificationsService;

    public void createConsultRequest(ConsultRequest consultRequest){
        consultRequest.setStatus(ConsultRequest.StatusEnum.PENDING);
        this.consultRequestRepository.save(consultRequest);
    }

    public ConsultRequest findById(int id) {
        return consultRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Yêu cầu tư vấn với Id: " + id + " không tìm thấy"));
    }

    public List<ConsultRequest> findAll() {
        return consultRequestRepository.findAll();
    }

    public void deleteById(int id) {
        if (!consultRequestRepository.existsById(id)) {
            throw new RuntimeException("Yêu cầu tư vấn với Id: " + id + " không tìm thấy");
        }
        consultRequestRepository.deleteById(id);
    }
}
