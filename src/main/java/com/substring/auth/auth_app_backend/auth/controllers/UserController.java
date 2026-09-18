package com.substring.auth.auth_app_backend.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.substring.auth.auth_app_backend.auth.config.AppConstants;
import com.substring.auth.auth_app_backend.auth.payload.UserDto;
import com.substring.auth.auth_app_backend.auth.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // Constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create User API
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userDto));
    }

    // Get All Users API
    @GetMapping
    public ResponseEntity<Iterable<UserDto>> getAllUser() {

        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    //get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email){
    	return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    
    //delete user
    @DeleteMapping("/userId")
    public void deleteUser(@PathVariable("userId") String userId) {
    	userService.deleteUser(userId);
    }
    
    //update user
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UserDto userDto,
            @PathVariable("userId") String userId) {

        return ResponseEntity.ok(userService.updateUser(userDto, userId));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('"+ AppConstants.ADMIN_ROLE +"')")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") String userId){
    	return ResponseEntity.ok(userService.getUserById(userId));
    }
    
}