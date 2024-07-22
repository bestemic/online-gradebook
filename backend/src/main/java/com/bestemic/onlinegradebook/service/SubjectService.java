package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.dto.subject.SubjectAddDto;
import com.bestemic.onlinegradebook.dto.subject.SubjectDto;
import com.bestemic.onlinegradebook.exception.CustomValidationException;
import com.bestemic.onlinegradebook.exception.NotFoundException;
import com.bestemic.onlinegradebook.mapper.SubjectMapper;
import com.bestemic.onlinegradebook.model.Subject;
import com.bestemic.onlinegradebook.repository.SubjectRepository;
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
