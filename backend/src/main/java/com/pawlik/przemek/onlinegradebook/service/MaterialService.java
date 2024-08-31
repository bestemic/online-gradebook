package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.repository.MaterialRepository;
import org.springframework.stereotype.Service;


@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }
}
