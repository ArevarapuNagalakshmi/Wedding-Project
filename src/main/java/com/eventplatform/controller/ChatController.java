package com.eventplatform.controller;

import com.eventplatform.dto.chat.ChatMessageDto;
import com.eventplatform.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ChatController {

    private final ChatService chatService;

    // ✅ Send message
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDto> send(
            @Valid @RequestBody ChatMessageDto msg,
            @AuthenticationPrincipal UserDetails userDetails) {

        // (Optional but recommended) ensure sender matches logged-in user
        // msg.setSenderId(authenticatedUserId);
        String username = userDetails.getUsername();

        Long authenticatedUserId = chatService.getUserIdByUsername(username);

        msg.setSenderId(authenticatedUserId);

        return ResponseEntity.ok(chatService.send(msg));
    }

    // ✅ Get chat history between two users
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @RequestParam Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Long authenticatedUserId = chatService.getUserIdByUsername(username);

        return ResponseEntity.ok(
                chatService.getChatHistory(authenticatedUserId,otherUserId));
    }
}
