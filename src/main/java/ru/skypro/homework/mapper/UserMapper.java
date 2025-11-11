package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "image", expression = "java(\"/users/\" + user.getId() + \"/image\")")
    UserDTO userToUserDto(User user);

    @Mapping(target = "image", ignore = true)
    User userDtoToUser(UserDTO userDTO);
}