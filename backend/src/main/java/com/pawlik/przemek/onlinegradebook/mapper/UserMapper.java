package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.user.UserAddDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserDto;
import com.pawlik.przemek.onlinegradebook.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    User userAddDtoToUser(UserAddDto userAddDto);

    UserDto userToUserDto(User user);
}