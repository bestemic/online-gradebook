package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.user.UserAddDto;
import com.bestemic.onlinegradebook.dto.user.UserDto;
import com.bestemic.onlinegradebook.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    User userAddDtoToUser(UserAddDto userAddDto);

    UserDto userToUserDto(User user);
}