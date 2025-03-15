package com.pawlik.przemek.onlinegradebook.service.file;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("!production")
public class LocalFileService implements FileService {

    private static final String BASE_UPLOAD_DIR = "uploads/";

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetDir = Paths.get(BASE_UPLOAD_DIR, directory);
            Path filePath = targetDir.resolve(fileName);
            Files.createDirectories(targetDir);

            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Please try again!");
        }
    }

    @Override
    public Resource getFile(String filePath) {
        try {
            Path fileStorageLocation = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(fileStorageLocation.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not download the file. Please try again!");
        }
    }
}
