package com.alibaba.imt.log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.alibaba.imt.config.AteyeServletConfig;
import com.alibaba.imt.manager.impl.LogControlManager;

public class Log4jFacility {
    private static Logger logger = Logger.getLogger("ateyeClient");
    
    public static void addAteyeClientLogger(){
        AteyeDailyRollingFileAppender appender = getAteyeClientAppender();
        Logger logger = Logger.getLogger("ateyeClient");
        logger.removeAllAppenders();
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);
        logger.addAppender(appender);
    }
    //add a customized error appender for all log4j loggers
    @SuppressWarnings("unchecked")
    public static void addCustomizedErrorAppender() {
        if ( ! AteyeServletConfig.isRecordLog4j ){
            logger.info("isRecordLog4j关闭,不监控Log4j的错误日志");
            return;
        }
        AteyeDailyRollingFileAppender appender = getCustomizedLog4jAppender();
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        if(loggers != null) {
            while (loggers.hasMoreElements()) {
                Logger logger = loggers.nextElement();
                Enumeration<Appender> allAppenders = logger.getAllAppenders();
                if(allAppenders != null && allAppenders.hasMoreElements()) {
                    if(!logger.getAdditivity()) {
                        logger.addAppender(appender);
                    }
                }
            }
        }
        //Log4j的root logger要特别处理
        Logger root = LogManager.getRootLogger();
        if(root != null) {
            root.addAppender(appender);
        }
    }

    public static Map<String,List<Appender>> getAllLoggerAppenders(){
        Map<String,List<Appender>> ret = new HashMap<String,List<Appender>>();
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        if(loggers != null) {
            while (loggers.hasMoreElements()) {
                Logger logger = loggers.nextElement();
                Enumeration<Appender> allAppenders = logger.getAllAppenders();
                if(allAppenders != null ){
                    ret.put(logger.getName(), new ArrayList<Appender>());
                    while ( allAppenders.hasMoreElements() ){
                        ret.get(logger.getName()).add(allAppenders.nextElement());
                    }
                }
            }
        }
        return ret;
    }
    private static AteyeDailyRollingFileAppender getCustomizedLog4jAppender() {
        AteyeDailyRollingFileAppender appender = new AteyeDailyRollingFileAppender();
        appender.setAppend(true);
        appender.setFile("/home/admin/ateye/error_log4j.log");
        appender.setEncoding("GBK");
        appender.setName("ateye_log4j");
        appender.setDatePattern("'.'yyyy-MM-dd-HH");
        PatternLayout pattern = new PatternLayout();
        pattern.setConversionPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}][%-5p] %c{2} [%F:%L] - %m%n");
        appender.setLayout(pattern);
        appender.setThreshold(Level.ERROR);
        appender.activateOptions();
        return appender;
    }
    private static AteyeDailyRollingFileAppender getAteyeClientAppender() {
        AteyeDailyRollingFileAppender appender = new AteyeDailyRollingFileAppender();
        appender.setAppend(true);
        appender.setFile("/home/admin/ateye/client.log");
        appender.setEncoding("GBK");
        appender.setName("ateye_client");
        appender.setDatePattern("'.'yyyy-MM-dd");
        PatternLayout pattern = new PatternLayout();
        pattern.setConversionPattern("[%X{start}][%X{uuid}][%d{yyyy-MM-dd HH:mm:ss.SSS}][%-5p] %c{2} [%F:%L] - %m%n");
        appender.setLayout(pattern);
        appender.activateOptions();
        return appender;
    }   
    public static List<LogVoBean> getAllLoggers() {
        List<LogVoBean> allLoggers = new ArrayList<LogVoBean>();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
            if(loggers != null) {
                while (loggers.hasMoreElements()) {
                    Logger logger = loggers.nextElement();
                    String name = logger.getName();
                    Level level = logger.getLevel();
                    if(level != null && StringUtils.isNotBlank(name)) {
                        LogVoBean log = new LogVoBean();
                        log.setName(logger.getName());
                        log.setLevel(level.toString());
                        log.setType(LogControlManager.LOGTYPE_LOG4J);
                        allLoggers.add(log);
                    }
                }
            }
            //Log4j的root logger要特别处理
            Logger root = LogManager.getRootLogger();
            if(root != null) {
                String name = root.getName();
                Level level = root.getLevel();
                if(level != null && StringUtils.isNotBlank(name)) {
                    LogVoBean log = new LogVoBean();
                    log.setName(root.getName());
                    log.setLevel(level.toString());
                    log.setType(LogControlManager.LOGTYPE_LOG4J);
                    allLoggers.add(log);
                }
            }
        } catch (Throwable ignore) {}
        return allLoggers;
    }
    
    public static void modifyLoggerLevel(String loggerName, String level) {
        try {
            Logger logger = Logger.getLogger(loggerName.trim());
            if(logger != null) {
                level = level.trim();
                if("INFO".equalsIgnoreCase(level)){
                    logger.setLevel(Level.INFO);
                }else if("DEBUG".equalsIgnoreCase(level)){
                    logger.setLevel(Level.DEBUG);
                }else if("WARN".equalsIgnoreCase(level)){
                    logger.setLevel(Level.WARN);
                }else if("ERROR".equalsIgnoreCase(level)){
                    logger.setLevel(Level.ERROR);
                }else if("FATAL".equalsIgnoreCase(level)){
                    logger.setLevel(Level.FATAL);
                }else if("ALL".equalsIgnoreCase(level)){
                    logger.setLevel(Level.ALL);
                }else if("OFF".equalsIgnoreCase(level)){
                    logger.setLevel(Level.OFF);
                }else if("TRACE".equalsIgnoreCase(level)){
                    logger.setLevel(Level.TRACE);
                }
            }
        } catch (Throwable ignore) {}
    }
    
}