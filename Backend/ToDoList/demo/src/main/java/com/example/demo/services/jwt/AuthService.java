package com.example.demo.services.jwt;

import com.example.demo.DTO.UserDTO;
import com.example.demo.entities.Users;
import com.example.demo.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createUser(UserDTO signupRequest) throws IOException {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return false;
        }
        Users user = new Users();
        //mapper.map(signupRequest,Employee.class);
        BeanUtils.copyProperties(signupRequest,user);

        String hashPassword=passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(hashPassword);
        userRepository.save(user);
        return true;
    }
}
