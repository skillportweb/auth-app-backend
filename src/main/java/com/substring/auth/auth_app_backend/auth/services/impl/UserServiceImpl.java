package com.substring.auth.auth_app_backend.auth.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.substring.auth.auth_app_backend.auth.config.AppConstants;
import com.substring.auth.auth_app_backend.auth.payload.UserDto;
import com.substring.auth.auth_app_backend.auth.entities.Provider;
import com.substring.auth.auth_app_backend.auth.entities.Role;
import com.substring.auth.auth_app_backend.auth.entities.User;
import com.substring.auth.auth_app_backend.exceptions.ResoureceNotFoundException;
import com.substring.auth.auth_app_backend.auth.helpers.UserHelper;
import com.substring.auth.auth_app_backend.auth.repositories.RoleRepository;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;
import com.substring.auth.auth_app_backend.auth.services.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            ModelMapper modelMapper,
            RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = modelMapper.map(userDto, User.class);

        user.setProvider(
                userDto.getProvider() != null
                        ? userDto.getProvider()
                        : Provider.LOCAL
        );

        // Assign default role
        Role role = roleRepository
                .findByName("ROLE_" + AppConstants.GUEST_ROLE)
                .orElseThrow(() -> new ResoureceNotFoundException(
                        "Guest role not found"
                ));

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResoureceNotFoundException(
                        "User is not found with given email id"
                ));

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        UUID uId = UserHelper.parseUUID(userId);

        User existingUser = userRepository.findById(uId)
                .orElseThrow(() -> new ResoureceNotFoundException(
                        "User not found with given id"
                ));

        // We are not going to change email id for this project

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getImage() != null) {
            existingUser.setImage(userDto.getImage());
        }

        if (userDto.getProvider() != null) {
            existingUser.setProvider(userDto.getProvider());
        }

        if (userDto.getPassword() != null) {
            existingUser.setPassword(userDto.getPassword());
        }

        existingUser.setEnable(userDto.isEnable());
        existingUser.setUpdateAt(Instant.now());

        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {

        UUID uid = UserHelper.parseUUID(userId);

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResoureceNotFoundException(
                        "User not found with given id"
                ));

        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(String userId) {

        User user = userRepository
                .findById(UserHelper.parseUUID(userId))
                .orElseThrow(() -> new ResoureceNotFoundException(
                        "User not found with given id"
                ));

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public Iterable<UserDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}