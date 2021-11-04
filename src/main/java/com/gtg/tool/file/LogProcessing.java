package com.gtg.tool.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogProcessing {

	private static String reportFileName;
	
	private static final Logger logger = LoggerFactory.getLogger(LogProcessing.class);

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		LogProcessing.reportFileName = reportFileName;
	}

	public static List<Pattern> initPatternList(){
	    String[] patternArray;
	    patternArray = new String [7];		
	    patternArray[0] = "Starting controlled shutdown";
		patternArray[1] = "Fatal error";
		patternArray[2] = "Attempt to heartbeat failed since group is rebalancing";
		patternArray[3] = "elected as controller instead of broker";
		patternArray[4] = "Could not find offset index file corresponding to log file";
		patternArray[5] = "WARN Cannot open channel to";
		patternArray[6] = "ERROR Unexpected exception causing shutdown";	    
	    
		List<Pattern> pList = new ArrayList<>(patternArray.length);
		
		for(String pattern:patternArray){
			Pattern pObj = new Pattern(pattern, 0);
			pList.add(pObj);
		}
		
		return pList;
	}
	
	public static List<Pattern> checkPatternMatchByLine(String line, List<Pattern> pList, int lineNumber) {
	
		for (Pattern pObj : pList) { 		      
			if(line.indexOf(pObj.getPattern())>-1) {
				pObj.setCnt(pObj.getCnt() + 1);
				pObj.setOutStr(pObj.getOutStr() + "["+ lineNumber + "] : " + line + "\n");
			}
		}
		
		return pList;
	}
	
	

	private static boolean isValidDate(String input) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
	          format.parse(input);
	          return true;
	     }
	     catch(ParseException e){
	          return false;
	     }
	}
	
	public static String readFile (String logFilePath, String startdate) {
		
		BufferedReader reader;
		String rtnStr = "";
		
		try {

			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String firstStr = "2021-03-05 08:31:04";
			
			startdate = startdate + " 00:00:00";
			
			reader = new BufferedReader(new FileReader(logFilePath));
			String line = reader.readLine();
			int lineNumber = 0;
			writeLog( "\n["+ logFilePath + "] Analysis\n");
			
			int cnt = 0;

			List<Pattern> pList =  initPatternList();
			
			while (line != null) {
				
					for (Pattern pObj : pList) { 		      
						if(line.indexOf(pObj.getPattern())>-1) {
							
							String secondStr = line.substring(1, 20);

							boolean before = false;
							
							if (isValidDate(secondStr)) {
								Date first = sdf.parse(startdate);
								Date second = sdf.parse(secondStr);
								before = (first.before(second));
								
								if (before == true) {
									pObj.setCnt(pObj.getCnt() + 1);
									writeLog( "["+ lineNumber + "] : " + line + "\n");
									cnt++;
								}
								
							} else {
								pObj.setCnt(pObj.getCnt() + 1);
								writeLog( "["+ lineNumber + "] : " + line + "\n");
								cnt++;
							}
						}
					}

				lineNumber++;
				line = reader.readLine();
			}
			
			if (cnt < 1) {
				writeLog(" ".repeat(21)+"N/A");
			} else {
				writeLog("-".repeat(80)+"\n");
	
				int pNo = 0;
				
				for (Pattern pObj : pList) { 	
					pNo ++;
					writeLog( " Pattern" + pNo + ": " + pObj.getCnt()); 
				}
				writeLog("\n");
				writeLog("-".repeat(80));
				
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rtnStr;
	}
	
	public static void checklog(String path, String startdate) {
      try {

    	analysis(path, startdate);
      	
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
	}
	
	
    public static String analysis(String path, String startdate) {

        File root = new File(path);
        File[] list = root.listFiles();
        
        String resStr = "";

        if (list == null) return resStr;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	analysis( f.getAbsolutePath(), startdate);
            }
            else {
            	resStr = resStr + readFile(f.getAbsoluteFile().toString(), startdate);
            }
        }

        return resStr;
    }
       
    public static void writeLog( String outputStr) {
    	
    	try {
	        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(reportFileName, true));
	        bufferedWriter.write(outputStr);
	        bufferedWriter.close();
    		
    	} catch (IOException e) {
    	    e.printStackTrace();
    	    System.out.println(e);
    	}    	
    }
	
}
