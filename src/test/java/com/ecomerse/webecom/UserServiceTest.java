package com.ecomerse.webecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecomerse.webecom.dto.LoginRequest;
import com.ecomerse.webecom.dto.RegisterRequest;
import com.ecomerse.webecom.dto.UserResponse;
import com.ecomerse.webecom.entity.Role;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.UnauthorizedException;
import com.ecomerse.webecom.repository.UserRepository;
import com.ecomerse.webecom.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerStoresEncodedPasswordAndLowercaseEmail() {
        when(userRepository.existsByEmail("asha@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        UserResponse response = userService.register(
                new RegisterRequest("Asha", "Asha@Example.com", "secret1", "9999999999"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("ENCODED", captor.getValue().getPassword());
        assertEquals("asha@example.com", captor.getValue().getEmail());
        assertEquals(Role.USER, response.role());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("asha@example.com")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.register(
                new RegisterRequest("Asha", "asha@example.com", "secret1", null)));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginFailsWithWrongPassword() {
        User user = new User();
        user.setEmail("asha@example.com");
        user.setPassword("ENCODED");
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("asha@example.com", "wrong")));
    }
}
