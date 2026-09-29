package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.AddressRequest;
import com.ecomerse.webecom.dto.AddressResponse;
import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.entity.Address;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.AddressRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponse add(User user, AddressRequest request) {
        Address address = new Address();
        address.setUser(user);
        apply(address, request);
        return Mappers.toAddress(addressRepository.save(address));
    }

    public AddressResponse update(User user, Long id, AddressRequest request) {
        Address address = findOwned(user, id);
        apply(address, request);
        return Mappers.toAddress(addressRepository.save(address));
    }

    public void delete(User user, Long id) {
        addressRepository.delete(findOwned(user, id));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAll(User user) {
        return addressRepository.findByUserId(user.getId()).stream().map(Mappers::toAddress).toList();
    }

    private Address findOwned(User user, Long id) {
        return addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void apply(Address address, AddressRequest request) {
        address.setFullName(request.fullName().trim());
        address.setPhone(request.phone().trim());
        address.setStreet(request.street().trim());
        address.setCity(request.city().trim());
        address.setState(request.state().trim());
        address.setPostalCode(request.postalCode().trim());
        address.setCountry(request.country().trim());
    }
}
