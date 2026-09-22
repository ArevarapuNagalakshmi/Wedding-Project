package com.eventplatform.service.chat;

import com.eventplatform.dto.chat.ChatMessageDto;
import com.eventplatform.entity.ChatMessage;
import com.eventplatform.entity.User;
import com.eventplatform.repository.ChatMessageRepository;
import com.eventplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public ChatMessageDto send(ChatMessageDto message) {

        //  Fetch sender
        User sender = userRepo.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        //  Fetch receiver
        User receiver = userRepo.findById(message.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        //  Save chat message
        ChatMessage chat = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(message.getContent())
                .timestamp(Instant.now())
                .build();

        chatRepo.save(chat);
        chatRepo.flush();

        //  Return DTO
        return ChatMessageDto.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .content(chat.getContent())
                .timestamp(chat.getTimestamp())
                .build();
    }

    @Override
    public List<ChatMessageDto> getChatHistory(Long userId, Long otherUserId) {

        return chatRepo
                .findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_Id(
                        userId, otherUserId,
                        otherUserId, userId
                )
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserIdByUsername(String username) {

        User user = userRepo.findByEmail(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        return user.getId();
    }




    private ChatMessageDto mapToDto(ChatMessage chat) {
        return ChatMessageDto.builder()
                .senderId(chat.getSender().getId())
                .receiverId(chat.getReceiver().getId())
                .content(chat.getContent())
                .timestamp(chat.getTimestamp())
                .build();

    }
}
