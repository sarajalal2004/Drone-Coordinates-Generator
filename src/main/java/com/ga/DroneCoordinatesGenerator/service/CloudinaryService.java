package com.ga.DroneCoordinatesGenerator.service;

import com.cloudinary.Cloudinary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private Cloudinary cloudinary;

    @Value("${cloudinary.cloudName}")
    private String cloudName;

    @Value("${cloudinary.apiKey}")
    private String apiKey;

    @Value("${cloudinary.apiSecret}")
    private String apiSecret;

    private final String profileUploadDir = "Drone/users/profiles/";

    @PostConstruct
    public void CloudinaryService(){
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadProfileImage(MultipartFile file, String email) throws IOException{
        Map param = ObjectUtils.asMap(
                "public_id", email,
                "asset_folder", profileUploadDir,
                "overwrite", true,
                "resource_type", "image"
        );
        Map result = cloudinary.uploader().upload(file.getBytes(), param);
        return (String) result.get("secure_url");
    }
}
