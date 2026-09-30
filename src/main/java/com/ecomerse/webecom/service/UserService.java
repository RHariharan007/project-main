package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.LoginRequest;
import com.ecomerse.webecom.dto.LoginResponse;
import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.dto.ProfileRequest;
import com.ecomerse.webecom.dto.RegisterRequest;
import com.ecomerse.webecom.dto.UserResponse;
import com.ecomerse.webecom.entity.Role;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.exception.UnauthorizedException;
import com.ecomerse.webecom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.USER);
        return Mappers.toUser(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return new LoginResponse("Login successful", Mappers.toUser(user));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        return Mappers.toUser(getByEmail(email));
    }

    public UserResponse updateProfile(String email, ProfileRequest request) {
        User user = getByEmail(email);
        user.setName(request.name().trim());
        user.setPhone(request.phone());
        return Mappers.toUser(userRepository.save(user));
    }
}
