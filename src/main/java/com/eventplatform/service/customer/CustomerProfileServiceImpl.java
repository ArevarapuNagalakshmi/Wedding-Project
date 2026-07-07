package com.eventplatform.service.customer;

import com.eventplatform.dto.customer.CustomerProfileDto;
import com.eventplatform.entity.CustomerProfile;
import com.eventplatform.entity.User;
import com.eventplatform.repository.CustomerProfileRepository;
import com.eventplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileDto getProfile(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        return customerProfileRepository.findByOwnerId(owner.getId())
                .map(this::mapToDto)
                .orElseGet(() -> mapToDto(owner));
    }

    @Override
    @Transactional
    public CustomerProfileDto updateProfile(String ownerEmail, CustomerProfileDto profileDto) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        CustomerProfile profile = customerProfileRepository.findByOwnerId(owner.getId())
                .orElse(CustomerProfile.builder().owner(owner).build());

        profile.setCity(profileDto.getCity());
        profile.setWeddingDate(profileDto.getWeddingDate());
        profile.setBudget(profileDto.getBudget());
        profile.setWeddingType(profileDto.getWeddingType());
        profile.setNotes(profileDto.getNotes());

        CustomerProfile savedProfile = customerProfileRepository.save(profile);
        return mapToDto(savedProfile);
    }

    private CustomerProfileDto mapToDto(CustomerProfile profile) {
        return CustomerProfileDto.builder()
                .id(profile.getId())
                .fullName(profile.getOwner().getFullName())
                .email(profile.getOwner().getEmail())
                .phone(profile.getOwner().getPhone())
                .city(profile.getCity())
                .weddingDate(profile.getWeddingDate())
                .budget(profile.getBudget())
                .weddingType(profile.getWeddingType())
                .notes(profile.getNotes())
                .build();
    }

    private CustomerProfileDto mapToDto(User owner) {
        return CustomerProfileDto.builder()
                .fullName(owner.getFullName())
                .email(owner.getEmail())
                .phone(owner.getPhone())
                .build();
    }
}
