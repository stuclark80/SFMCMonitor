/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 *
 * @author Daviess1
 */
public class DIRExecuter implements Job {
    

@Override
    public void execute(JobExecutionContext context)
    throws JobExecutionException {
       //DISABLED AS NO ACCESS TO ANZ DIRS ACROSS DOMAIN; THIS WON'T WORK 
     //   FileHandler fH = new FileHandler();
     //   fH.dirGO();
        
    }
    

}
