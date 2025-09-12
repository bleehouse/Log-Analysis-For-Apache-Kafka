package com.bleehouse.tool.parser;

import com.bleehouse.tool.model.LogEntry;
import com.bleehouse.tool.model.ParseResult;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface LogParser {
    
    /**
     * 로그 파일을 파싱하여 결과를 반환합니다.
     */
    ParseResult parseLog(InputStream inputStream, String patternGroup, LocalDateTime startDate);
    
    /**
     * 지원하는 패턴 그룹 목록을 반환합니다.
     */
    List<String> getSupportedPatternGroups();
    
    /**
     * 파서가 지원하는 로그 형식인지 확인합니다.
     */
    boolean canParse(String fileName, InputStream inputStream);
    
    /**
     * 파서의 우선순위를 반환합니다. (높을수록 우선)
     */
    int getPriority();
}