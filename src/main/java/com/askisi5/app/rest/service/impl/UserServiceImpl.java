package com.askisi5.app.rest.service.impl;

import com.askisi5.app.rest.dto.UserRequest;
import com.askisi5.app.rest.dto.UserResponse;
import com.askisi5.app.rest.exception.ResourceNotFoundException;
import com.askisi5.app.rest.mapper.UserMapper;
import com.askisi5.app.rest.model.User;
import com.askisi5.app.rest.repository.UserRepository;
import com.askisi5.app.rest.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(long id) {
        logger.info("Fetching user with id: {}", id);
        User user = findUserOrThrow(id);
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        logger.info("Creating new user: {} {}", request.getFirstName(), request.getLastName());
        User savedUser = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(long id, UserRequest request) {
        logger.info("Updating user with id: {}", id);
        User user = findUserOrThrow(id);
        UserMapper.copyToEntity(request, user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(long id) {
        logger.info("Deleting user with id: {}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User with id {} not found", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
    }
}
