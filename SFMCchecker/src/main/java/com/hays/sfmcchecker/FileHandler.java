/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Daviess1
 */
public class FileHandler {

        //***********************************************************************************
        //******************  REWRITE THIS CLASS TO USE CONFIG FILE**************************
        //***********************************************************************************
    
    public static void listDirsInFolder(File folder) {
        
        String filePathString;
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String ext = ".csv";

       TreeMap<String, Boolean> results =new TreeMap<String, Boolean>();
       String[] regions = {"AU", "NZ", "CN", "MY", "SG", "JP", "HK"}; 
        


        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                
                String region = fileEntry.getName();
                if ( ArrayUtils.contains( regions, region ) ) {
                                filePathString = folder+region+"\\Application_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString)));

                                filePathString = folder+region+"\\Candidate_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString)));   

                                filePathString = folder+region+"\\Contact_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString))); 

                                filePathString = folder+region+"\\Placement_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString))); 

                                filePathString = folder+region+"\\Job_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString))); 

                                filePathString = folder+region+"\\Consultant_"+region+"_"+date+ext;
                                results.put(filePathString, Files.exists(Paths.get(filePathString))); 
                }
     
            } else {
                
                continue;
            }
        }
        //PRINT RESULTS
        
        results.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
  if(1==1){
    //BUILD THE HTML FOR EMAIL
          StringBuilder sB = new StringBuilder("<HTML>");
          sB.append("<TABLE>");
          sB.append("<TH>LOCATION</TH><TH>EXIST IN DIR</TH>");

          //sort data for formatting
          DataSorter dS = new DataSorter();
          TreeMap<String,Boolean> sortedFiles = dS.sortDIR(results);

          Iterator it = sortedFiles.entrySet().iterator();
          while (it.hasNext()) {
              Map.Entry pair = (Map.Entry)it.next();

              String s = pair.getKey().toString();
              if (s.contains("\\\\SY4OPR01.hays.com.au\\c$\\SFMC\\iManageExport\\")){
                //int i = s.indexOf("#");
                s = s.replace("\\\\SY4OPR01.hays.com.au\\c$\\SFMC\\iManageExport\\", "");
              }



              sB.append("<TR><TD>"+s+"</TD>");

              if (pair.getValue() == null){
                  sB.append("<TD>-</TD></TR>");
                  it.remove();
                  continue;
              }else if (Boolean.TRUE.equals(pair.getValue())){

                  sB.append("<TD style='background-color:green;'>"+pair.getValue().toString().toUpperCase()+"</TD></TR>");
              } else {
                  sB.append("<TD style='background-color:red;'>"+pair.getValue().toString().toUpperCase()+"</TD></TR>");
              }
              //+pair.getValue()+"</TD</TR>");
              //System.out.println(pair.getKey() + " = " + pair.getValue());
              it.remove(); // avoids a ConcurrentModificationException
          }
          sB.append("</TABLE></HTML>");
          String html =sB.toString();

          System.out.println("SimpleEmail Start");

          MailSender mS = new MailSender();
          mS.sendMail(html, date, "DIR");
  }      
        
        
        
        
        
        
    }
    
    public static void ftpGO(){
    
        
            System.out.println("START FTP CHECK");
            //final File baseFolder = new File ("\\\\HRMVMPD3726\\iManage_Exports");
             try {
                 // listDirsInFolder(folder);
                 FtpHandler fTp =new FtpHandler();
                 fTp.checkFTP();
                 System.out.println("FINISH FTP CHECK");
             } catch (IOException ex) {
                 Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
             }
        
    }
        public static void dirGO(){
    
        
            System.out.println("START DIR CHECK");
            final File baseFolder = new File ("\\\\SY4OPR01.hays.com.au\\c$\\SFMC\\iManageExport\\");
            // listDirsInFolder(folder);
            FtpHandler fTp =new FtpHandler();
            listDirsInFolder(baseFolder);
            System.out.println("FINISH DIR CHECK");
        
    }

    
}
