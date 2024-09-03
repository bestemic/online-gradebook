package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.material.MaterialAddDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialDto;
import com.pawlik.przemek.onlinegradebook.mapper.MaterialMapper;
import com.pawlik.przemek.onlinegradebook.model.Material;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.repository.MaterialRepository;
import com.pawlik.przemek.onlinegradebook.service.file.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final FileUploadService fileUploadService;
    private final SubjectService subjectService;

    public MaterialService(MaterialRepository materialRepository, MaterialMapper materialMapper, FileUploadService fileUploadService, SubjectService subjectService) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
        this.fileUploadService = fileUploadService;
        this.subjectService = subjectService;
    }

    public MaterialDto createMaterial(MaterialAddDto materialAddDto, MultipartFile file) {
        Subject subject = subjectService.getSubjectObjectById(materialAddDto.getSubjectId());

        String directory = "materials/subject_" + subject.getId();
        String filePath = fileUploadService.uploadFile(file, directory);

        Material material = new Material();
        material.setName(materialAddDto.getName());
        material.setDescription(materialAddDto.getDescription());
        material.setFilePath(filePath);
        material.setPublicationTime(LocalDateTime.now());
        material.setSubject(subject);

        Material savedMaterial = materialRepository.save(material);
        return materialMapper.materialToMaterialDto(savedMaterial);
    }
}
