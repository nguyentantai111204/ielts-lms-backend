package com.ntt.lms.controller;

import com.ntt.lms.pojo.ConsultRequest;
import com.ntt.lms.pojo.Location;
import com.ntt.lms.service.AdminService;
import com.ntt.lms.service.ConsultRequestService;
import com.ntt.lms.service.LocationService;
import com.ntt.lms.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult-request")
public class ConsultRequestController {

    private final ConsultRequestService consultRequestService;
    private final NotificationsService notificationsService;
    private final LocationService locationService;
    private final AdminService adminService;

    @PostMapping
    public void createConsultRequest(@RequestBody ConsultRequest consultRequest){
        consultRequestService.createConsultRequest(consultRequest);
        int facilityAdminId = adminService.findAdminFacilityAdmin(consultRequest.getLocation().getLocationId());
        String message = "Có người cần tư vấn ngay luc này!";
        notificationsService.sendNotification(message, facilityAdminId);
    }

    @GetMapping("/all_location")
    public ResponseEntity<?> getAllLocations() {
        try {
            List<Location> locationList = locationService.getAllLocations();
            return ResponseEntity.ok(locationList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
