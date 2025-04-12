package com.aol.AiPropertyVeiwingSystem.persistence.repository;

import com.aol.AiPropertyVeiwingSystem.persistence.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findBySessionId(String sessionId);

    @Query(name = "ChatSession.findRecentSessionsByUserId")
    List<ChatSession> findRecentSessionsByUserId(@Param("userId") Long userId);

    @Query(name = "ChatSession.findInactiveSessions")
    List<ChatSession> findInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);
} 