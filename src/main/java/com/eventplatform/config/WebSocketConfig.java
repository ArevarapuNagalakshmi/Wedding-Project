package com.eventplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configure message broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Clients SUBSCRIBE to these destinations
        config.enableSimpleBroker("/topic", "/queue");

        // Clients SEND messages to these destinations
        config.setApplicationDestinationPrefixes("/app");
    }

    // WebSocket endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // fallback for older browsers
    }
}
