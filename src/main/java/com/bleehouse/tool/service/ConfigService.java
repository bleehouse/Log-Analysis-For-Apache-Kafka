package com.bleehouse.tool.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.bleehouse.tool.config.LogPatternConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    private LogPatternConfig config;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    @PostConstruct
    public void loadConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("log-patterns.yml");
            try (InputStream inputStream = resource.getInputStream()) {
                config = yamlMapper.readValue(inputStream, LogPatternConfig.class);
                logger.info("로그 패턴 설정을 성공적으로 로드했습니다. 패턴 그룹 수: {}", 
                           config.getPatterns().size());
            }
        } catch (IOException e) {
            logger.error("로그 패턴 설정 로드 실패", e);
            throw new RuntimeException("설정 파일을 로드할 수 없습니다", e);
        }
    }
    
    public LogPatternConfig getConfig() {
        return config;
    }
    
    public LogPatternConfig.PatternGroup getPatternGroup(String groupName) {
        return config.getPatterns().get(groupName);
    }
}