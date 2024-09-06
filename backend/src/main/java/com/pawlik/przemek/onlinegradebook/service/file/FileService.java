package com.pawlik.przemek.onlinegradebook.service.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String directory);

    Resource getFile(String filePath);
}