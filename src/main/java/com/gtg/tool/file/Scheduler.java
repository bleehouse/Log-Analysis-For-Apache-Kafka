package com.gtg.tool.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class Scheduler {
	
	private final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	
	
	// 1초에 한번 실행된다.
	@Scheduled(fixedDelay = 5000) 
	public void scheduleFixedRateTask() {
		

	    logger.info(String.format("Fixed rate task - " + System.currentTimeMillis() / 5000));
	    
	}

}
