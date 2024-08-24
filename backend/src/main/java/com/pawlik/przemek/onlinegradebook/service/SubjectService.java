package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectAddDto;
import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.SubjectMapper;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    @Transactional
    public SubjectDto createSubject(SubjectAddDto subjectAddDto) {
        if (subjectRepository.findByName(subjectAddDto.getName()).isPresent()) {
            throw new CustomValidationException("name", "Subject with provided name already exists");
        }

        Subject subject = subjectMapper.subjectAddDtoToSubject(subjectAddDto);
        subjectRepository.save(subject);
        return subjectMapper.subjectToSubjectDto(subject);
    }

    public List<SubjectDto> getAllSubjects() {
        return ((List<Subject>) subjectRepository.findAll()).stream()
                .map(subjectMapper::subjectToSubjectDto)
                .collect(Collectors.toList());
    }

    public SubjectDto getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found with ID: " + subjectId));
        return subjectMapper.subjectToSubjectDto(subject);
    }
}
