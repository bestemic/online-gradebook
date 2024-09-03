package com.pawlik.przemek.onlinegradebook.service.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("production")
public class GCSFileUploadService implements FileUploadService {

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        return null;
    }
}
