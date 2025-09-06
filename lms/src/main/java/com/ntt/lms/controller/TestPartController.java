package com.ntt.lms.controller;

import com.ntt.lms.service.TestPartService;
import com.ntt.lms.pojo.TestPart;
import com.ntt.lms.repository.TestPartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test-parts")
@RequiredArgsConstructor
public class TestPartController {

    private final TestPartService testPartService;
    private final TestPartRepository testPartRepository;


    @PostMapping("/{partId}/upload-audio")
    public ResponseEntity<?> uploadAudio(
            @PathVariable int partId,
            @RequestParam("audio") MultipartFile audio) {

        try {
            String audioUrl = testPartService.uploadAudioForPart(partId, audio);
            return ResponseEntity.ok().body(audioUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{partId}")
    public ResponseEntity<?> getPart(@PathVariable int partId) {
        return testPartRepository.findById(partId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
