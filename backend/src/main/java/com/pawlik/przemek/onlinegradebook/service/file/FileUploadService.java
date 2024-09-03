package com.pawlik.przemek.onlinegradebook.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file, String directory);
}
