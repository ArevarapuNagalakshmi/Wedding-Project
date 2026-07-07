package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_sender", columnList = "sender_id"),
                @Index(name = "idx_chat_receiver", columnList = "receiver_id"),
                @Index(name = "idx_chat_time", columnList = "timestamp")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    @Lob
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Instant timestamp;
}
