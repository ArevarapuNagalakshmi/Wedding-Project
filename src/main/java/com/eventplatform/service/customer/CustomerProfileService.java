package com.eventplatform.service.customer;

import com.eventplatform.dto.customer.CustomerProfileDto;

public interface CustomerProfileService {

    CustomerProfileDto getProfile(String ownerEmail);

    CustomerProfileDto updateProfile(String ownerEmail, CustomerProfileDto profileDto);
}
