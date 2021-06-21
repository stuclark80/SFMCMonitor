/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import org.quartz.CronScheduleBuilder;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Daviess1
 */
public class Main {

        private static final Logger logger = LogManager.getLogger(Main.class);
        
        private static Config config = new Config();
        private static final int FTP_TRIGGER_HOUR = Integer.valueOf(config.getProperty("FTP_TRIGGER_HOUR"));
        private static final int FTP_TRIGGER_MIN = Integer.valueOf(config.getProperty("FTP_TRIGGER_MIN"));
        private static final int DIR_TRIGGER_HOUR = Integer.valueOf(config.getProperty("DIR_TRIGGER_HOUR"));
        private static final int DIR_TRIGGER_MIN = Integer.valueOf(config.getProperty("DIR_TRIGGER_MIN"));

        

        public static void main(String[] args) throws Exception {

        logger.info("**START**");
        logger.info("FTP TRIGGER TIME: "+FTP_TRIGGER_HOUR+":"+FTP_TRIGGER_MIN);
        logger.info("DIR TRIGGER TIME: "+DIR_TRIGGER_HOUR+":"+DIR_TRIGGER_MIN);

        JobKey jobKeyA = new JobKey("FileChecker", "group1");
        JobDetail jobA = JobBuilder.newJob(FTPExecuter.class)
                .withIdentity(jobKeyA).build();

        logger.info("JOB A - Built");
            
        JobKey jobKeyB = new JobKey("DirChecker", "group1");
        JobDetail jobB = JobBuilder.newJob(DIRExecuter.class)
                .withIdentity(jobKeyB).build();
            
        logger.info("JOB B - Built");  
            
            Trigger triggerA = TriggerBuilder
                .newTrigger()
                .withIdentity("FTPCheckerTrigger", "group1")
                .withSchedule(
                    dailyAtHourAndMinute(FTP_TRIGGER_HOUR, FTP_TRIGGER_MIN))
                .build();

        logger.info("FTP TRIGGER - Built");    	
    
            Trigger triggerB = TriggerBuilder
                .newTrigger()
                .withIdentity("DIRCheckerTrigger", "group1")
                .withSchedule(
                    dailyAtHourAndMinute(DIR_TRIGGER_HOUR, DIR_TRIGGER_MIN))
                .build();
        
        logger.info("DIR TRIGGER - Built");        
            
            //schedule it
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobA, triggerA);
            //scheduler.scheduleJob(jobB, triggerB);
        
        logger.info("SCHEDULERS - READY");    

     }
       
    
    
 
    
    
    
    
    
    
    
    
        
        
}
