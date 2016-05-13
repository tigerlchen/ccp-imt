package com.alibaba.imt.manager;

import java.io.PrintStream;
import java.util.Map;

import javax.servlet.ServletContext;

import com.alibaba.imt.constants.ManagerType;

public interface AteyeManager
{
    /**
     * Manager的实际操作方法
     * @param queryParams 请求参数的Map
     * @return 返回的操作结果字符串
     */
    public String service(Map<String,String> queryParams);
    
    /**
     * Manager的初始化方法，传入Servlet的上下文环境
     * @param servletContext Servlet上下文环境
     */
    public void init(ServletContext servletContext, PrintStream initLogger,Map<String, Object> beans);
    
    /**
     * 标示该Manager的业务类型，返回枚举类型
     * @return ManagerType.SWITCH | ManagerType.QUARTZ | ManagerType.LOGCONTROL
     */
    public ManagerType getType();
}
