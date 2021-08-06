package com.gtg.tool.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogProcessing {

	private static String reportFileName;

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		LogProcessing.reportFileName = reportFileName;
	}

	public static List<Pattern> initPatternList(){
	    String[] patternArray;
	    patternArray = new String [6];		
	    patternArray[0] = "Starting controlled shutdown";
		patternArray[1] = "Fatal error";
		patternArray[2] = "Attempt to heartbeat failed since group is rebalancing";
		patternArray[3] = "elected as controller instead of broker";
		patternArray[4] = "java.net.ConnectException: Connection refused (Connection refused)";
		patternArray[5] = "ERROR Unexpected exception causing shutdown";	    
	    
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
	
	public static String readFile (String logFilePath) {
		
		BufferedReader reader;
		String rtnStr = "";
		
		try {
			reader = new BufferedReader(new FileReader(logFilePath));
			String line = reader.readLine();
			int lineNumber = 0;
			writeLog( "\n["+ logFilePath + "] Analysis\n");
			
			int cnt = 0;

			List<Pattern> pList =  initPatternList();
			
			while (line != null) {
				
				for (Pattern pObj : pList) { 		      
					if(line.indexOf(pObj.getPattern())>-1) {
						pObj.setCnt(pObj.getCnt() + 1);
						writeLog( "["+ lineNumber + "] : " + line + "\n");
						cnt++;
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
		}
		return rtnStr;
	}
	
	public static void checklog(String path) {
      try {

    	analysis(path);
      	
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
	}
	
	
    public static String analysis(String path) {

        File root = new File(path);
        File[] list = root.listFiles();
        
        String resStr = "";

        if (list == null) return resStr;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	analysis( f.getAbsolutePath());
            }
            else {
            	resStr = resStr + readFile(f.getAbsoluteFile().toString());
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
