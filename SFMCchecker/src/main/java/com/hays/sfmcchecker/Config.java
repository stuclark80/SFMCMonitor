/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hays.sfmcchecker;

import java.util.Properties;
import java.lang.Thread;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Config {

    private static final Logger logger = LogManager.getLogger(Config.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            logger.info("Loading config file...");
            PROPERTIES.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            logger.info("Loaded config file");
        } catch (IOException e) {
            logger.error("Loading config file failed.", e);
        }
    }

    public static String getProperty(String key) {
        logger.debug("Fteching property: "+key);
        return PROPERTIES.getProperty(key);
    }

    // ...
}
