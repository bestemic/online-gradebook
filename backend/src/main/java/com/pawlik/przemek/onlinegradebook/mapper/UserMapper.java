package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.user.AddUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUserDto;
import com.pawlik.przemek.onlinegradebook.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {RoleMapper.class, SchoolClassMapper.class})
public interface UserMapper {
    User userAddDtoToUser(AddUserDto addUserDto);

    @Mapping(source = "schoolClass.id", target = "assignedClassId")
    GetUserDto userToUserDto(User user);
}
