package com.eventplatform.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final RestTemplate restTemplate;

    @Value("${fast2sms.api.key}")
    private String apiKey;

    @Override
    public void sendOtp(String mobileNumber, String otp) {

        try {

            String url = "https://www.fast2sms.com/dev/bulkV2";

            HttpHeaders headers = new HttpHeaders();

            headers.set("authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{"
                    + "\"variables_values\":\"" + otp + "\","
                    + "\"route\":\"otp\","
                    + "\"numbers\":\"" + mobileNumber + "\""
                    + "}";

            HttpEntity<String> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            System.out.println("SMS SENT SUCCESSFULLY");
            System.out.println("Response: " + response.getBody());

        } catch (Exception e) {

            System.out.println("SMS FAILED");
            e.printStackTrace();

            throw new RuntimeException("Failed to send SMS");

        }

    }
}
