package com.pawlik.przemek.onlinegradebook.service.file;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("production")
public class CloudFileService implements FileService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CloudFileService.class);
    private final Storage storage;
    private final String bucketName;

    public CloudFileService(@Value("${gcp.bucket.name}") String bucketName) {
        this.bucketName = bucketName;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        String fileName = directory + "/" + file.getOriginalFilename();

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            LOGGER.error("Error sending file to cloud storage", e);
            throw new RuntimeException("Could not store the file. Please try again!");
        }
        return fileName;
    }

    @Override
    public Resource getFile(String filePath) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, filePath));
            if (blob == null) {
                return null;
            }
            InputStream inputStream = new ByteArrayInputStream(blob.getContent());
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            LOGGER.error("Error retrieving file from cloud storage", e);
            throw new RuntimeException("Could not retrieve the file. Please try again!");
        }
    }
}
