package com.aol.AiPropertyVeiwingSystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "openai.api")
@Getter
@Setter
public class OpenAiConfig {
    private String key;
    private String model;
    private double temperature;
    private int maxTokens;
} 