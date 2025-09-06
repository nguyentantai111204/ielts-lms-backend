package com.ntt.lms.service;

import com.ntt.lms.pojo.TestPart;
import com.ntt.lms.repository.TestPartRepository;
import com.ntt.lms.utils.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TestPartService {
    private final CloudinaryService cloudinaryService;
    private final TestPartRepository testPartRepository;

    public String uploadAudioForPart(int partId, MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("File audio không được rỗng");
        }

        TestPart part = testPartRepository.findById(partId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy part với ID: " + partId));

        try {
            // Upload audio lên Cloudinary
            String audioUrl = cloudinaryService.uploadFile(audioFile, "test_parts", "video");

            // Lưu URL vào entity
            part.setAudioUrl(audioUrl);
            testPartRepository.save(part);

            return audioUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi upload audio", e);
        }
    }
}
