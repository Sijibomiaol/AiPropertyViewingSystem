package com.aol.AiPropertyVeiwingSystem.persistence.entity;

import com.aol.AiPropertyVeiwingSystem.enums.ChatSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
    @NamedQuery(
        name = "ChatSession.findInactiveSessions",
        query = "SELECT cs FROM ChatSession cs WHERE cs.status = 'ACTIVE' AND cs.lastActive < :cutoffTime"
    ),
    @NamedQuery(
        name = "ChatSession.findRecentSessionsByUserId",
        query = "SELECT cs FROM ChatSession cs WHERE cs.user.id = :userId ORDER BY cs.lastActive DESC"
    )
})
public class ChatSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatSessionStatus status;

    @Column(name = "last_active", nullable = false)
    private LocalDateTime lastActive;

    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setChatSession(this);
        this.lastActive = LocalDateTime.now();
    }

    public void removeMessage(ChatMessage message) {
        messages.remove(message);
        message.setChatSession(null);
    }

    public void updateLastActive() {
        this.lastActive = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (lastActive == null) {
            lastActive = LocalDateTime.now();
        }
    }
} 