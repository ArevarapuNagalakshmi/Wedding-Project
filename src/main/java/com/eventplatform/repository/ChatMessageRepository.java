package com.eventplatform.repository;
import com.eventplatform.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_Id(
            Long sender1, Long receiver1,
            Long sender2, Long receiver2
    );

    List<ChatMessage> findBySender_Id(Long senderId);

    List<ChatMessage> findByReceiver_Id(Long receiverId);
}
