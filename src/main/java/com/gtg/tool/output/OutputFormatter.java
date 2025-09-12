package com.gtg.tool.output;

import com.gtg.tool.model.ParseResult;

import java.util.List;

public interface OutputFormatter {
    
    /**
     * 분석 결과를 포맷팅합니다.
     */
    String format(List<ParseResult> results);
    
    /**
     * 지원하는 출력 형식을 반환합니다.
     */
    String getFormatType();
    
    /**
     * 파일 확장자를 반환합니다.
     */
    String getFileExtension();
}