package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.class_group.ClassGroupDto;
import com.bestemic.onlinegradebook.model.ClassGroup;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ClassGroupMapper {

    ClassGroupDto classGroupToClassGroupDto(ClassGroup classGroup);
}