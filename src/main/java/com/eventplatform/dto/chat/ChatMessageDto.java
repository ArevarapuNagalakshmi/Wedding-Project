package com.eventplatform.dto.chat;
import com.eventplatform.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    @NotBlank(message = "Message content cannot be empty")
    private String content;
    private Instant timestamp;
}
