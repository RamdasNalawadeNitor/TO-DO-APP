package com.example.demo.controllers;

import com.example.demo.DTO.TaskDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.entities.ApiResponse;
import com.example.demo.services.TaskService;
import com.example.demo.services.jwt.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/signup")
@CrossOrigin
public class SignUpController {

    private final AuthService authService;

    @Autowired
    private TaskService taskService;

    public SignUpController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/addUser")
    public ResponseEntity<ApiResponse> addNewUser(@ModelAttribute UserDTO userDTO) throws IOException {
        boolean isUserCreated=authService.createUser(userDTO);
        if(isUserCreated){
            return ResponseEntity.ok().body(new ApiResponse("New User Added"));
        }
        else{
            return ResponseEntity.status(500).body(new ApiResponse("Unable to add user"));
        }
//
    }
}
