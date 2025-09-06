package com.ntt.lms.service;

import com.ntt.lms.pojo.Admin;
import com.ntt.lms.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    public int findAdminFacilityAdmin(int locationId){
        Admin admin = adminRepository.findFirstByLocation_LocationId(locationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin với locationId: " + locationId));
        return admin.getUserAccountId();
    }
}
