/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Daviess1
 */
public class FtpHandler {
    
    private static final Logger logger = LogManager.getLogger(FtpHandler.class);

    private static Config config = new Config();
    private static final String dFormat = config.getProperty("DATE_FORMAT");
    private static final String dirRegions = config.getProperty("REGIONS");    
    private static final String PROXY_URL = config.getProperty("PROXY_URL");
    private static final String PROXY_USER = config.getProperty("PROXY_USER");
    private static final String PROXY_PASSWORD = config.getProperty("PROXY_PASSWORD");
    private static final int PROXY_PORT = Integer.valueOf(config.getProperty("PROXY_PORT"));
    private static final DateFormat dateFormat = new SimpleDateFormat(dFormat);
    private static final String FTP_URL = config.getProperty("FTP_URL");
    private static final int FTP_PORT = Integer.valueOf(config.getProperty("FTP_PORT"));
    private static final String FTP_USERNAME = config.getProperty("FTP_USERNAME");
    private static final String FTP_PASSWORD = config.getProperty("FTP_PASSWORD");
    private static final String FTP_LOOK_IN_DIR = config.getProperty("FTP_LOOK_IN_DIR");

    
    public static void checkFTP() throws IOException {
        
      //  String pattern = "yyyyMMdd";
      //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      //  String dateS = simpleDateFormat.format(new Date());
      //  System.out.println(dateS);
                

        
        Calendar cal = Calendar.getInstance();

        //Add one day to current date.
        cal.add(Calendar.DATE, -1);
        logger.info("TIME:" + dateFormat.format(cal.getTime()));
        //System.out.println(dateFormat.format(cal.getTime()));
    
        String dateS = dateFormat.format(cal.getTime()).toString();
        logger.info("TIME STRING:" + dateS);
        //System.out.println(dateS);
        
       // String reg = "AU";
        logger.info("BUILDING FTP HANDLER");
        FtpHandler ftpH = new FtpHandler();
        TreeMap<String, Boolean> files = ftpH.listFileNames(FTP_URL, FTP_PORT, FTP_USERNAME, FTP_PASSWORD, FTP_LOOK_IN_DIR, dateS);
        //System.out.println(files);
        logger.info(files);
        
        //BUILD THE HTML FOR EMAIL
        logger.info("BUILDING HTML");
        StringBuilder sB = new StringBuilder("<HTML>");
        sB.append("<TABLE>");
        sB.append("<TH>LOCATION</TH><TH>EXIST IN FTP</TH>");
        
        //sort data for formatting
        Collection<String> regionsL = new DefaultListDelimiterHandler(',').split(dirRegions, true);
        String[] regions = regionsL.toArray(new String[regionsL.size()]);

        DataSorter dS = new DataSorter();
        TreeMap<String,Boolean> sortedFiles = dS.sort(files, regions);
        
        Iterator it = sortedFiles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String s = pair.getKey().toString();
            if (s.contains("#")){
              int i = s.indexOf("#");
              s = s.substring((i+1),s.length());
            }
           
            if ((pair.getKey() == null)||(pair.getKey().toString().length() < 2)){
                continue;
            }
            sB.append("<TR><TD>"+s+"</TD>");

                       
            if (pair.getValue() == null){
                sB.append("<TD>-</TD></TR>");
                it.remove();
                continue;
            } else if (Boolean.TRUE.equals(pair.getValue())){
                sB.append("<TD style='background-color:green;'>"+pair.getValue().toString().toUpperCase()+"</TD></TR>");
            } else if (Boolean.FALSE.equals(pair.getValue())){
                sB.append("<TD style='background-color:red;'>"+pair.getValue().toString().toUpperCase()+"</TD></TR>");
            }
            //+pair.getValue()+"</TD</TR>");
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        sB.append("</TABLE></HTML>");
        String html =sB.toString();
        
        logger.info("BUILT HTML");
        logger.debug(html);

        //System.out.println("SimpleEmail Start");
		
        MailSender mS = new MailSender();
        mS.sendMail(html, dateS, "FTP");
    }
    
    
    public static TreeMap<String, Boolean> listFileNames(String host, int port, String username, final String password, String dir, String date) {
        //List<String> list = new ArrayList<String>();
        
        
        
        
        TreeMap<String, Boolean> list = new TreeMap<String, Boolean>(); 
        list.clear();
        Collection<String> regionsL = new DefaultListDelimiterHandler(',').split(dirRegions, true);
        String[] regions = regionsL.toArray(new String[regionsL.size()]);
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;
        
        try {

            logger.info("FTP SESSION: STARTING");

            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            
            //
            ProxyHTTP  proxy = new ProxyHTTP(PROXY_URL,PROXY_PORT);
            proxy.setUserPasswd(PROXY_USER,PROXY_PASSWORD);
            sshSession.setProxy(proxy);
            
            //sshSession.setProxy(new ProxyHTTP("http://Svc-proxy-prod:PrdPa$$Pr0xy99!@proxy.emea.hays.loc", 8080));
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            logger.info("FTP SESSION: CONNECTED");
            //System.out.println("Session connected!");
            logger.info("FTP CHANNEL: STARTING");
            channel = sshSession.openChannel("sftp");
            channel.connect();
            logger.info("FTP CHANNEL: CONNECTED");
            //System.out.println("Channel connected!");
            sftp = (ChannelSftp) channel;

             
        
            for(String region: regions){ 
                //System.out.println(region); 
                
                //logger.info("****REGION DEBUG*****");
                //logger.info(region);
                String regionF = new String();
                if(region.length()>2){
                    regionF = region.substring(0,2);
                    //logger.info(region);
                } else {
                    regionF = region;
                }
                logger.info("CHECKING REGION: " +regionF+ "|||| OLDREGION: "+region);
                list.put("Job_"+regionF+"_"+date+".csv", false);
                list.put("Contact_"+regionF+"_"+date+".csv", false);
                list.put("Candidate_"+regionF+"_"+date+".csv", false);
                list.put("Placement_"+regionF+"_"+date+".csv", false);
                list.put("Consultant_"+regionF+"_"+date+".csv", false);
                list.put("Application_"+regionF+"_"+date+".csv", false);

                logger.info("CHECKING DIRECTORY: " +dir+region);

                Vector<?> vector= sftp.ls(dir+region);
                for (Object item:vector) {
                    LsEntry entry = (LsEntry) item;

                    //System.out.println(entry.getFilename());
                    if (("Job_"+regionF+"_"+date+".csv").equals(entry.getFilename()) || ("Contact_"+regionF+"_"+date+".csv").equals(entry.getFilename()) || ("Candidate_"+regionF+"_"+date+".csv").equals(entry.getFilename()) || ("Placement_"+regionF+"_"+date+".csv").equals(entry.getFilename()) || ("Consultant_"+regionF+"_"+date+".csv").equals(entry.getFilename()) || ("Application_"+regionF+"_"+date+".csv").equals(entry.getFilename()) ){
                        //System.out.println(entry.getFilename()+ "TRUE");
                        logger.info("ENTRY: " +entry.getFilename()+ "TRUE");
                        list.replace(entry.getFilename(), true);
                    }

                }
            }    
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("{}", e);
            
        } finally {
            closeChannel(sftp);
            logger.info("FTP: CLOSED");
            //System.out.println("ftp Closed!");
            closeChannel(channel);
            logger.info("FTP CHANNEL: CLOSED");
            //System.out.println("Channel Closed!");
            closeSession(sshSession);
            //System.out.println("Session Closed!");
            logger.info("FTP SESSION: CLOSED");
        }

        logger.info("CREATED LIST: "+ list);
        return list;
    }
 
    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
 
    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    
    
}
