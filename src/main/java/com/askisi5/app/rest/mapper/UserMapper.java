package com.askisi5.app.rest.mapper;

import com.askisi5.app.rest.dto.UserRequest;
import com.askisi5.app.rest.dto.UserResponse;
import com.askisi5.app.rest.model.User;

/**
 * Manual mapping between the User entity and its request/response DTOs.
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(UserRequest request) {
        User user = new User();
        copyToEntity(request, user);
        return user;
    }

    public static void copyToEntity(UserRequest request, User user) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStreet(request.getStreet());
        user.setCity(request.getCity());
        user.setPostalCode(request.getPostalCode());
        user.setCountry(request.getCountry());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthdayDate(request.getBirthdayDate());
        user.setSex(request.getSex());
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setStreet(user.getStreet());
        response.setCity(user.getCity());
        response.setPostalCode(user.getPostalCode());
        response.setCountry(user.getCountry());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBirthdayDate(user.getBirthdayDate());
        response.setSex(user.getSex());
        return response;
    }
}
