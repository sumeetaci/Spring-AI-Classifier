package com.restai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restai.dto.UserRegistrationDto;
import com.restai.entity.User;
import com.restai.models.AuthResponse;
import com.restai.repositories.UserRepository;
import com.restai.services.JwtService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository; 
    
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String message) {}
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
    	User user = new User();
    			user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setUserEmail(registrationDto.getEmail());
        user.setRole("ROLE_USER"); // Assign a default role
       if( userRepository.findByUsername(user.getUsername() ) != null) {
			throw new RuntimeException("Username already exists");
		}
        userRepository.save(user); 
        return ResponseEntity.ok("User created successfully ");
    }
   
    // TODO This method not needed. 
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    	try {
    	Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
    	loginRequest.username(), loginRequest.password()));
    	// 2. Generate the token
        String jwt = jwtService.generateToken(authentication.getName());

        // 3. Return the token in a JSON format (so jq can parse it)
        return ResponseEntity.ok(new AuthResponse(jwt));
        
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid username or password");
        }
    }
}
