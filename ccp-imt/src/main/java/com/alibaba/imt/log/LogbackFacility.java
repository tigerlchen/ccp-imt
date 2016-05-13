package com.alibaba.imt.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.alibaba.imt.manager.impl.LogControlManager;

import com.alibaba.imt.manager.impl.LogControlManager;

public class LogbackFacility {
    
    public static final String SP = (char)18 + "";
    
    //add a customized error appender for all logback loggers
    public static void addCustomizedErrorAppender() {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(loggerFactory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            List<Logger> loggerList = loggerContext.getLoggerList();
            if(loggerList != null && loggerList.size() > 0) {
                RollingFileAppender<ILoggingEvent> appender = getCustomizedLogbackAppender(loggerContext);
                for(Logger logger : loggerList) {
                    Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
                    if(appenders.hasNext()) { // 有appender的logger才处理，过滤掉框架自带的没有log文件输出的logger
                        if("ROOT".equalsIgnoreCase(logger.getName())) {
                            logger.addAppender(appender);
                        } else {
                            if(!logger.isAdditive()) {
                                logger.addAppender(appender);
                            }
                        }
                    }
                }
            }
        }
    }
     
    private static RollingFileAppender<ILoggingEvent> getCustomizedLogbackAppender(LoggerContext loggerContext) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
        appender.setFile("/home/admin/ateye/error_logback.log");
        //Encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder() {
            @Override()
            public void doEncode(ILoggingEvent event) throws IOException {
                Level level = event.getLevel();
                if(Level.ERROR.equals(level)) {
                    String rawMsg = layout.doLayout(event);
                    if(StringUtils.isNotBlank(rawMsg)) {
                        rawMsg = rawMsg.replace(CoreConstants.LINE_SEPARATOR, SP);
                        outputStream.write(rawMsg.getBytes());
                        outputStream.flush();
                    }
                }
            }
        };
        encoder.setPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}][%level] %logger{35} [%file:%line] - %m%n");
        encoder.setContext(loggerContext);
        encoder.start();
        //RollingPolicy
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern("/home/admin/ateye/error_logback.log.%d{yyyy-MM-dd-HH}");
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setMaxHistory(6);//最多保留6份
        rollingPolicy.start();
        //assemble appender
        appender.setEncoder(encoder);
        appender.setRollingPolicy(rollingPolicy);
        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }
        
    public static List<LogVoBean> getAllLoggers() {
        List<LogVoBean> allLoggers = new ArrayList<LogVoBean>();
        try {
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            if(loggerFactory instanceof LoggerContext) {
                LoggerContext loggerContext = (LoggerContext) loggerFactory;
                List<Logger> loggerList = loggerContext.getLoggerList();
                if(loggerList != null && loggerList.size() > 0) {
                    for(Logger logger : loggerList) {
                        Level level = logger.getLevel();
                        String name = logger.getName();
                        if(level != null && StringUtils.isNotBlank(name)) {
                            LogVoBean log = new LogVoBean();
                            log.setName(logger.getName());
                            log.setLevel(level.toString());
                            log.setType(LogControlManager.LOGTYPE_LOGBACK);
                            allLoggers.add(log);
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}
        return allLoggers;
    }
    
    public static void modifyLoggerLevel(String loggerName, String level) {
        try {
            Logger logger = (Logger) LoggerFactory.getLogger(loggerName.trim());
            if(logger != null) {
                level = level.trim();
                if("INFO".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.INFO);
                } else if("DEBUG".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
                } else if("WARN".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.WARN);
                } else if("ERROR".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.ERROR);
                } else if("TRACE".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.TRACE);
                } else if("ALL".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.ALL);
                } else if("OFF".equalsIgnoreCase(level)) {
                    logger.setLevel(ch.qos.logback.classic.Level.OFF);
                }
            }
        } catch (Throwable ignore) {}
    }
    
}
