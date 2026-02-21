package com.origin.converter;

import com.origin.dto.UserDTO;
import com.origin.entity.User;

public class UserConverter {

    public static UserDTO convertToUserDTO(User user){

        return UserDTO.builder().id(user.getId()).
                name(user.getName()).
                password("******").
                email(user.getEmail()).
                create_time(user.getCreate_time()).
                update_time(user.getUpdate_time()).
                status(user.getStatus()).
                avatar(user.getAvatar()).build();
    }

    public static User convertToUser(UserDTO userDTO){

        User user = new User();

        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus());
        user.setAvatar(userDTO.getAvatar());

        return user;
    }
}
