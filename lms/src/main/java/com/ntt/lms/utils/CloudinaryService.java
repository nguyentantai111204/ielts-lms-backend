package com.ntt.lms.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }

    public String uploadAudio(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video"
                ));
        return (String) uploadResult.get("secure_url");
    }


    public String uploadFile(MultipartFile file, String folder, String resourceType) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", resourceType
                ));
        return (String) uploadResult.get("secure_url");
    }


    public void deleteFile(String fileUrlOrPublicId) throws IOException {
        if (fileUrlOrPublicId == null || fileUrlOrPublicId.isEmpty()) return;

        String publicId = fileUrlOrPublicId;
        if (fileUrlOrPublicId.startsWith("http")) {
            int lastSlash = fileUrlOrPublicId.lastIndexOf("/");
            int lastDot = fileUrlOrPublicId.lastIndexOf(".");
            if (lastSlash >= 0 && lastDot > lastSlash) {
                publicId = fileUrlOrPublicId.substring(lastSlash + 1, lastDot);
            }
        }

        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("Cloudinary delete result: " + result);
    }
}
