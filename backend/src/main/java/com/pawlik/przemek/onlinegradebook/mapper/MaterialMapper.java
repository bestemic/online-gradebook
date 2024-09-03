package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.material.MaterialDto;
import com.pawlik.przemek.onlinegradebook.model.Material;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MaterialMapper {

    MaterialDto materialToMaterialDto(Material material);
}