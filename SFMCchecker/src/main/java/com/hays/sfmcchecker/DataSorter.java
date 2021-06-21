/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Daviess1
 */
public class DataSorter {
    
        private static final Logger logger = LogManager.getLogger(DataSorter.class);
        //***********************************************************************************
        //******************  REWRITE THIS CLASS TO USE CONFIG FILE**************************
        //***********************************************************************************

        public TreeMap<String, Boolean> sort(TreeMap<String, Boolean> files, String[] regions){
            
         TreeMap<String, Boolean> newMap = new TreeMap<String, Boolean>();

         for (String region : regions) {

            if(region.length()>2){
               
               logger.debug("SORTER DEBUG ***********************************************"); 
               logger.debug("OLD REGION: "+region); 
               String regionN = new String(region.substring(0,2));
               region = regionN;
               logger.debug("NEW REGION: "+region);
               logger.debug("SORTER DEBUG END *******************************************");

               
            } 


            newMap.put(region, null);
        }
  
        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String oldK = new String();
            String newK = new String();

            oldK = String.valueOf(pair.getKey());
            for (String region : regions) {

                logger.debug("ERROR OLDK REGION: "+oldK+ " ||| "+region);

                if(region.length()>2){

                   logger.debug("SORTER UK DEBUG ***********************************************"); 
                   logger.debug("OLD UK REGION: "+region); 
                   String regionN = new String(region.substring(0,2));
                   region = regionN;
                   logger.debug("NEW UK REGION: "+region);
                   logger.debug("SORTER UK DEBUG END *******************************************");


                } 





                if (oldK.contains(region))

                    newK = region+"#"+oldK;
                    newMap.put(newK, (Boolean) pair.getValue());
            }


            
        }
        return newMap;        
    }
     public TreeMap<String, Boolean> sortDIR(TreeMap<String, Boolean> files){
            
         TreeMap<String, Boolean> newMap = new TreeMap<String, Boolean>();
         newMap.put("AU\\", null);
         newMap.put("NZ\\", null);
         newMap.put("CN\\", null);
         newMap.put("MY\\", null);
         newMap.put("SG\\", null);
         newMap.put("JP\\", null);
         newMap.put("HK\\", null);
 

         
        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String oldK = new String();
            String newK = new String();

            oldK = String.valueOf(pair.getKey());
            if (oldK.contains("\\\\SY4OPR01.hays.com.au\\c$\\SFMC\\iManageExport\\")){
                //int i = s.indexOf("#");
                oldK = oldK.replace("\\\\SY4OPR01.hays.com.au\\c$\\SFMC\\iManageExport\\", "");
              }
            newK = oldK;

            newMap.put(newK, (Boolean) pair.getValue());
        } 
        return newMap;      


    }    
        
}