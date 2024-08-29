package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.user.UserAddDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserDto;
import com.pawlik.przemek.onlinegradebook.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {RoleMapper.class, SchoolClassMapper.class})
public interface UserMapper {
    User userAddDtoToUser(UserAddDto userAddDto);

    @Mapping(source = "schoolClass.id", target = "classId")
    UserDto userToUserDto(User user);
}