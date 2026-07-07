package com.eventplatform.service.auth;


import com.eventplatform.entity.AadhaarVerification;

public interface AadhaarService {

    AadhaarVerification registerAadhaar(
            String aadhaarNumber,
            String fullName,
            String mobileNumber
    );
    AadhaarVerification getByAadhaar(String aadhaarNumber);
}
