package com.substring.auth.auth_app_backend.product.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    public String saveImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image to upload"
            );
        }

        // Validate image content type
        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }

        // Validate file extension
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() == null
                        ? "image"
                        : file.getOriginalFilename()
        );

        String extension =
                StringUtils.getFilenameExtension(originalFilename);

        if (extension == null ||
                !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid image file extension"
            );
        }

        // Create upload directory automatically
        Path uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadPath);

        // Generate a unique filename
        String filename = UUID.randomUUID()
                + "."
                + extension.toLowerCase();

        Path targetPath = uploadPath.resolve(filename).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        // Save image to uploads/products
        Files.copy(file.getInputStream(), targetPath);

        // Return the path to save in the database
        return "/uploads/products/" + filename;
    }
}