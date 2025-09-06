package com.ntt.lms.controller;

import com.ntt.lms.pojo.Location;
import com.ntt.lms.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;


    @GetMapping("/all")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }


    @PostMapping("/add")
    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
        Location created = locationService.addLocation(location);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable int id,
            @RequestBody Location updatedLocation
    ) {
        Location updated = locationService.updateLocation(id, updatedLocation);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable int id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok("Xóa location thành công");
    }
}
