package com.eventplatform.service.chat;

import com.eventplatform.dto.chat.ChatMessageDto;
import java.util.List;

public interface ChatService {
    ChatMessageDto send(ChatMessageDto message);
    List<ChatMessageDto> getChatHistory(Long userId, Long otherUserId);
    Long getUserIdByUsername(String username);

}
