package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.material.MaterialAddDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialDto;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.MaterialMapper;
import com.pawlik.przemek.onlinegradebook.model.Material;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.repository.MaterialRepository;
import com.pawlik.przemek.onlinegradebook.service.file.FileService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final FileService fileService;
    private final SubjectService subjectService;

    public MaterialService(MaterialRepository materialRepository, MaterialMapper materialMapper, FileService fileService, SubjectService subjectService) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
        this.fileService = fileService;
        this.subjectService = subjectService;
    }

    public MaterialDto createMaterial(MaterialAddDto materialAddDto, MultipartFile file) {
        Subject subject = subjectService.getSubjectObjectById(materialAddDto.getSubjectId());

        String directory = "materials/subject_" + subject.getId();
        String filePath = fileService.uploadFile(file, directory);

        Material material = new Material();
        material.setName(materialAddDto.getName());
        material.setDescription(materialAddDto.getDescription());
        material.setFilePath(filePath);
        material.setPublicationTime(LocalDateTime.now());
        material.setSubject(subject);

        Material savedMaterial = materialRepository.save(material);
        return materialMapper.materialToMaterialDto(savedMaterial);
    }

    public List<MaterialDto> getMaterialsBySubject(Long subjectId) {
        subjectService.getSubjectObjectById(subjectId);

        List<Material> materials = materialRepository.findBySubjectId(subjectId);
        return materials.stream()
                .map(materialMapper::materialToMaterialDto)
                .toList();
    }

    public Resource getMaterialFile(Long materialId) {
        Material material = materialRepository.findById(materialId).orElseThrow(() -> new NotFoundException("Material not found with id " + materialId));
        Resource file = fileService.getFile(material.getFilePath());

        if (file == null) {
            throw new NotFoundException("Not found file associated to material with id " + materialId);
        }
        return file;
    }
}
