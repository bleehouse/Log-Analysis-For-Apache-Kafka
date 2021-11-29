package com.gtg.tool.runner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gtg.tool.file.LogProcessing;

@Component
public class BrokerAnalysis implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(BrokerAnalysis.class);
	
	@Override
	public void run(String... args) throws Exception {

		logger.info("Confluent Log Analysis Start");
		
		String logFilePath = args[0];
		
		String startdate = args[1];
		
//		if (args[1])
		
		String defaultFileName = "confluent_report.log";
		this.makeReport(defaultFileName, logFilePath, startdate);
		this.getFinalFile(defaultFileName);
		
		logger.info("Complete!!");
	}
	
	public void makeReport(String defaultFileName, String logFilePath, String startdate) {
		LogProcessing logProcessiong = new LogProcessing(); 
		
		logProcessiong.setReportFileName(defaultFileName);
		
		logProcessiong.writeLog("*".repeat(160)+ "\n");
		logProcessiong.writeLog(" ".repeat(10) + this.getTodayString() + " ".repeat(10) + "Confluent Monthly Log Analysis Report\n");
		logProcessiong.writeLog("*".repeat(160)+ "\n\r");
		logProcessiong.analysis(logFilePath, startdate);
		
		this.genDesc(logProcessiong);
	}
	
	public void genDesc(LogProcessing logProcessiong) {
		logProcessiong.writeLog("\n"); 
		logProcessiong.writeLog("*".repeat(160)+ "\n");
		
	    String[] descArray;
	    descArray = new String [7];
	    descArray[0]  = "[B] Pattern1 : \"Starting controlled shutdown\""+ "\n";
	    descArray[1]  = "[B] Pattern2 : \"Fatal error\""+ "\n";
	    descArray[2]  = "[B] Pattern3 : \"Attempt to heartbeat failed since group is rebalancing\""+ "\n";
	    descArray[3]  = "[B] Pattern4 : \"elected as controller instead of broker\""+ "\n";
	    descArray[4]  = "[B] Pattern5 : \"Could not find offset index file corresponding to log file\""+ "\n";
	    descArray[5]  = "[Z] Pattern6 : \"WARN Cannot open channel to\""+ "\n";
	    descArray[6]  = "[Z] Pattern7 : \"ERROR Unexpected exception causing shutdown\""+ "\n";
		
		for(String desc:descArray){
			logProcessiong.writeLog(desc);
		}
	
		logProcessiong.writeLog("*".repeat(160)+ "\n");
	}
	
	public void getFinalFile(String defaultFileName) {
        
		File final_file = new File(defaultFileName);
		Date date_now = new Date(System.currentTimeMillis());
		SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMddHHmmss"); 
		String report_filename = fourteen_format.format(date_now) + "_" + defaultFileName; 

        File outputDir = new File("reports"); 
        
        if(!outputDir.exists()) { 
        	outputDir.mkdir(); 
        }

        final_file.renameTo(new File(outputDir + final_file.separator + report_filename));
        
	}
	
	public String getTodayString() {
		
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Calendar c1 = Calendar.getInstance();
      String strToday = sdf.format(c1.getTime());
		
      return strToday;
	}

}
