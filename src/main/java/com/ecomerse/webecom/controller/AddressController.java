package com.ecomerse.webecom.controller;

import com.ecomerse.webecom.dto.AddressRequest;
import com.ecomerse.webecom.dto.AddressResponse;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.service.AddressService;
import com.ecomerse.webecom.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll(Authentication authentication) {
        return ResponseEntity.ok(addressService.getAll(currentUser(authentication)));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> add(@Valid @RequestBody AddressRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.add(currentUser(authentication), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody AddressRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(addressService.update(currentUser(authentication), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, Authentication authentication) {
        addressService.delete(currentUser(authentication), id);
        return ResponseEntity.ok(Map.of("message", "Address deleted"));
    }

    private User currentUser(Authentication authentication) {
        return userService.getByEmail(authentication.getName());
    }
}
