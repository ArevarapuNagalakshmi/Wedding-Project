package com.eventplatform.service.auth;


public interface SmsService {

    void sendOtp(String mobileNumber, String otp);

}
