package com.example.demo.controllers;

import com.example.demo.DTO.SignInResponse;
import com.example.demo.DTO.SigninDTO;
import com.example.demo.entities.Users;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.jwt.JwtService;
import com.example.demo.services.jwt.UserJwtImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJwtImpl userJwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SigninDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails user;
        Optional<Users> userOptional;
        try {
            user = userJwt.loadUserByUsername(loginDTO.getEmail());
            userOptional = userRepository.findByEmail(loginDTO.getEmail());
            Users user1 = userOptional.get();
            System.out.println("User logged in !");

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
        String jwt = jwtService.generateToken(user);
        System.out.println(jwt);

        return ResponseEntity.ok(new SignInResponse(jwt));
    }
}

//    public ResponseEntity<?> loginUser(@RequestBody SigninDTO loginRequest) {
//        System.out.println("recieved request for LOGIN USER");
//        System.out.println(loginRequest);
//        try {
//            SignInResponse response = employeeService.loginEmp(loginRequest.getEmail(),loginRequest.getPassword(),loginRequest.getRole());
//            System.out.println("Sign In Response is : "+response);
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
