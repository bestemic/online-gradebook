package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassDto;
import com.pawlik.przemek.onlinegradebook.model.SchoolClass;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SchoolClassMapper {

    SchoolClassDto schoolClassToSchoolClassDto(SchoolClass schoolClass);
}