package com.eventplatform.service.auth;

import com.eventplatform.entity.AadhaarVerification;
import com.eventplatform.repository.AadhaarVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AadhaarServiceImpl implements AadhaarService {

    private final AadhaarVerificationRepository repository;

    // ✅ Aadhaar validation
    private boolean isValidAadhaar(String aadhaar) {
        return aadhaar != null && aadhaar.matches("\\d{12}");
    }

    @Override
    public AadhaarVerification registerAadhaar(
            String aadhaarNumber,
            String fullName,
            String mobileNumber) {

        // ✅ Trim all inputs
        aadhaarNumber = aadhaarNumber.trim();
        fullName = fullName.trim();
        mobileNumber = mobileNumber.trim();

        // ✅ Validate Aadhaar
        if (!isValidAadhaar(aadhaarNumber)) {
            throw new RuntimeException("Invalid Aadhaar number");
        }

        // ✅ Validate mobile
        if (!mobileNumber.matches("\\d{10}")) {
            throw new RuntimeException("Invalid mobile number");
        }

        // ✅ Validate name
        if (fullName.isEmpty()) {
            throw new RuntimeException("Full name is required");
        }

        // ✅ Check existing Aadhaar
        AadhaarVerification aadhaar = repository
                .findByAadhaarNumber(aadhaarNumber)
                .orElse(null);

        if (aadhaar != null) {

            // ❌ If already verified → block
            if (aadhaar.isActive()) {
                throw new RuntimeException("Aadhaar already verified");
            }

            // ✅ Update existing unverified record
            aadhaar.setFullName(fullName);
            aadhaar.setMobileNumber(mobileNumber);

        } else {
            // ✅ Create new record
            aadhaar = new AadhaarVerification();
            aadhaar.setAadhaarNumber(aadhaarNumber);
            aadhaar.setFullName(fullName);
            aadhaar.setMobileNumber(mobileNumber);
            aadhaar.setActive(false);
        }

        return repository.save(aadhaar);
    }

    @Override
    public AadhaarVerification getByAadhaar(String aadhaarNumber) {

        if (aadhaarNumber == null || aadhaarNumber.trim().isEmpty()) {
            throw new RuntimeException("Aadhaar number is required");
        }

        aadhaarNumber = aadhaarNumber.trim();

        return repository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() ->
                        new RuntimeException("Aadhaar not registered"));
    }
}