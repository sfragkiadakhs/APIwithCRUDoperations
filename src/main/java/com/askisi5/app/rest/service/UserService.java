package com.askisi5.app.rest.service;

import com.askisi5.app.rest.dto.UserRequest;
import com.askisi5.app.rest.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(long id);

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(long id, UserRequest request);

    void deleteUser(long id);
}
