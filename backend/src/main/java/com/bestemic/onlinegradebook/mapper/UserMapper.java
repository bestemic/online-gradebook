package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.UserAddDto;
import com.bestemic.onlinegradebook.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User userAddDtoToUser(UserAddDto userAddDto);
}