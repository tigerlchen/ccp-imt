package com.alibaba.imt.manager.impl;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.imt.config.AteyeServletConfig;
import com.alibaba.imt.constants.ManagerType;
import com.alibaba.imt.log.LogVoBean;
import com.alibaba.imt.manager.AteyeManager;
import com.alibaba.imt.util.JsonUtils;

public class LogControlManager implements AteyeManager{
    private static Logger logger = Logger.getLogger("ateyeClient");

    public static final String LOGTYPE_LOG4J ="LOG4J";

    public static final String LOGTYPE_LOGBACK ="LOGBACK";

    private static final String[] logFacilities = {"com.taobao.ateye.log.LogbackFacility", "com.taobao.ateye.log.Log4jFacility"};

    @Override
    public String service(Map<String, String> queryParams) {
        String json = null;
        List<LogVoBean> allLoggers = null;
        String operate = queryParams.get("operate");
        if(StringUtils.isBlank(operate)) {
            operate = "getLoggers";
        }
        if("getLoggers".equalsIgnoreCase(operate)) {
            allLoggers = getAllLoggers();
            json = JsonUtils.jsonEncode(allLoggers);
        } else if("modifyLogger".equalsIgnoreCase(operate)) {
            String name = queryParams.get("name");
            String level =  queryParams.get("level");
            String logtype = queryParams.get("logtype");
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(level) && StringUtils.isNotBlank(logtype)) {
                String logFacility = null;
                if(LOGTYPE_LOGBACK.equalsIgnoreCase(logtype.trim())) {
                    logFacility = "com.taobao.ateye.log.LogbackFacility";
                } else if(LOGTYPE_LOG4J.equalsIgnoreCase(logtype.trim())) {
                    logFacility = "com.taobao.ateye.log.Log4jFacility";
                }
                if(StringUtils.isNotBlank(logFacility)) {
                    try {
                        Class<?> logFacilityClass = Class.forName(logFacility);
                        if(logFacilityClass != null) {
                            Method modifyLoggerLevelMethod = logFacilityClass.getMethod("modifyLoggerLevel", new Class[] {String.class, String.class});
                            if(modifyLoggerLevelMethod != null) {
                                modifyLoggerLevelMethod.invoke(null, new Object[] {name, level});
                            }
                        }
                    } catch (Throwable ignore) {}
                }
            }
            allLoggers = getAllLoggers();
            json = JsonUtils.jsonEncode(allLoggers);
        }
        return json;

    }

    @Override
    public void init(ServletContext servletContext, PrintStream initLogger, Map<String, Object> beans) {
        if ( AteyeServletConfig.isRecordErrorLogger ){
            for(String logFacility : logFacilities) {
                try {
                    Class<?> logFacilityClass = Class.forName(logFacility);
                    logger.info("应用支持:"+logFacility);
                    if(logFacilityClass != null) {
                        Method addCustomizedErrorAppenderMethod = logFacilityClass.getMethod("addCustomizedErrorAppender", (Class<?>[])null);
                        if(addCustomizedErrorAppenderMethod != null) {
                            addCustomizedErrorAppenderMethod.invoke(null, (Object[])null);
                            logger.info(logFacility+"添加appender成功");
                        }
                    }
                }catch (NoClassDefFoundError e){
                    logger.warn("应用不支持:"+logFacility);
                }catch (ClassNotFoundException e){
                    logger.warn("应用不支持:"+logFacility);
                }catch (Throwable t) {
                    logger.error(logFacility+"添加appender异常",t);
                }
            }
        }

    }

    @Override
    public ManagerType getType() {
        return ManagerType.LOGCONTROL;
    }

    private List<LogVoBean> getAllLoggers(){
        List<LogVoBean> allLoggers = new ArrayList<LogVoBean>();
        for(String logFacility : logFacilities) {
            try {
                Class<?> logFacilityClass = Class.forName(logFacility);
                if(logFacilityClass != null) {
                    Method getAllLoggersMethod = logFacilityClass.getMethod("getAllLoggers", (Class<?>[])null);
                    if(getAllLoggersMethod != null) {
                        @SuppressWarnings("unchecked")
                        List<LogVoBean> allLogbackLoggers = (List<LogVoBean>) getAllLoggersMethod.invoke(null, (Object[])null);
                        if(allLogbackLoggers != null) {
                            allLoggers.addAll(allLogbackLoggers);
                        }
                    }
                }
            } catch (Throwable ignore) {}
        }
        return allLoggers;
    }

}

