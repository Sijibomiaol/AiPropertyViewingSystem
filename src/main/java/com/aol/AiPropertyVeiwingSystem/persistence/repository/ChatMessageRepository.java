package com.aol.AiPropertyVeiwingSystem.persistence.repository;


import com.aol.AiPropertyVeiwingSystem.persistence.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<com.aol.AiPropertyVeiwingSystem.persistence.entity.ChatMessage, Long> {
} 