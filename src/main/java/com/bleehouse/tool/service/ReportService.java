package com.bleehouse.tool.service;

import com.bleehouse.tool.model.ParseResult;
import com.bleehouse.tool.output.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final String REPORTS_DIR = "reports";
    
    @Autowired
    private List<OutputFormatter> outputFormatters;
    
    private Map<String, OutputFormatter> formatterMap;
    
    public void initializeFormatters() {
        if (formatterMap == null) {
            formatterMap = outputFormatters.stream()
                .collect(Collectors.toMap(
                    OutputFormatter::getFormatType,
                    Function.identity()
                ));
        }
    }
    
    /**
     * 분석 결과를 지정된 형식으로 리포트를 생성합니다.
     */
    public String generateReport(List<ParseResult> results, String outputFormat, String baseFileName) {
        initializeFormatters();
        
        OutputFormatter formatter = formatterMap.get(outputFormat.toUpperCase());
        if (formatter == null) {
            throw new IllegalArgumentException("지원하지 않는 출력 형식: " + outputFormat);
        }
        
        String content = formatter.format(results);
        String fileName = generateFileName(baseFileName, formatter.getFileExtension());
        
        saveReport(fileName, content);
        
        logger.info("리포트 생성 완료: {}", fileName);
        return fileName;
    }
    
    private String generateFileName(String baseFileName, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("%s_%s%s", timestamp, baseFileName, extension);
    }
    
    private void saveReport(String fileName, String content) {
        try {
            File reportsDir = new File(REPORTS_DIR);
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            File reportFile = new File(reportsDir, fileName);
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(content);
            }
            
        } catch (IOException e) {
            logger.error("리포트 저장 실패: {}", fileName, e);
            throw new RuntimeException("리포트 저장 실패", e);
        }
    }
    
    /**
     * 사용 가능한 출력 형식 목록을 반환합니다.
     */
    public List<String> getAvailableFormats() {
        initializeFormatters();
        return formatterMap.keySet().stream().sorted().toList();
    }
}