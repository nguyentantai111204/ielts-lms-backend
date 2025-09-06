package com.ntt.lms.service;

import com.ntt.lms.pojo.Location;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.LocationRepository;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserValidator userValidator;
    private final AdminValidator adminValidator;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location addLocation(Location location) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        if (location.getLocationName() == null || location.getLocationName().isBlank()) {
            throw new IllegalArgumentException("Tên location không được để trống");
        }

        return locationRepository.save(location);
    }


    public Location updateLocation(int id, Location updatedLocation) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy location với id: " + id));

        if (updatedLocation.getLocationName() != null && !updatedLocation.getLocationName().isBlank()) {
            location.setLocationName(updatedLocation.getLocationName());
        }

        return locationRepository.save(location);
    }


    public void deleteLocation(int id) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy location với id: " + id));
        locationRepository.delete(location);
    }
}
