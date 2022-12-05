package com.hcx.hcxPayor.controller;


import com.hcx.hcxPayor.dto.UserRequestDTO;
import com.hcx.hcxPayor.dto.UserResponseDTO;
import com.hcx.hcxPayor.model.User;
import com.hcx.hcxPayor.repository.UserRepo;
import com.hcx.hcxPayor.service.impl.UserServiceImpl;
import com.hcx.hcxPayor.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payor/user")
public class UserController {

     @Autowired
     private UserRepo userRepo;

     @Autowired
     private JwtUtil jwtUtil;

     @Autowired
     private UserServiceImpl userServiceImpl;


     @Autowired
     private AuthenticationManager authenticationManager;

    @PostMapping("/addUser")
    public ResponseEntity<String> addNewUser(@RequestBody User user){
        String id= userServiceImpl.saveUser(user);
        return  ResponseEntity.ok("User Added"+ id);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO){

        try {
        Authentication s =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.getUserName(), userRequestDTO.getPassword()));
        }
        catch(BadCredentialsException e){
           log.error(String.valueOf(e));

        }
        UserDetails userDetails= userServiceImpl.loadUserByUsername(userRequestDTO.getUserName());
        UserResponseDTO userResponseDTO= new UserResponseDTO();
        String token=jwtUtil.generateToken(userDetails);
        userResponseDTO.setToken(token);
        userResponseDTO.setMessage("Success");

       return ResponseEntity.ok(userResponseDTO);
    }
}
