package com.alibaba.imt.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.imt.constants.ManagerType;

public class ManagerLoggerUtil
{
    //日志容量限制：1M
    public static final int _MEM_LOG_LIMIT = 1024*1024;

    //具体Manager的日志输出流
    public static final Map<ManagerType,AteyeOutputStream> streamMap = new HashMap<ManagerType,AteyeOutputStream>();
    //Bootstrap的日志输出流
    private static AteyeOutputStream bootstrapStream = null;
    //具体Manager的启动日志
    public static final Map<ManagerType,String> initLogs = new HashMap<ManagerType,String>();
    //系统启动日志（获得bean容器日志）
    public static String bootstrapLog = null;

    //获得一般Manager的启动日志Logger
    public static PrintStream getManagerLogger(ManagerType managerType)
    {
        AteyeOutputStream ateyeOutputStream = new AteyeOutputStream(new ByteArrayOutputStream(), _MEM_LOG_LIMIT);
        streamMap.put(managerType, ateyeOutputStream);
        return new PrintStream(ateyeOutputStream, true);
    }


    //获得系统启动日志Logger
    public static synchronized PrintStream getBootstrapLogger()
    {
        if(bootstrapStream ==null)
        {
            bootstrapStream = new AteyeOutputStream(new ByteArrayOutputStream(), _MEM_LOG_LIMIT);
        }
        return new PrintStream(bootstrapStream, true);
    }

    //将系统启动日志Logger置为无效
    public static void invalidBootstrapStream()
    {
        if(bootstrapStream!=null)
        {
            bootstrapStream.invalid();
            bootstrapLog = new String(bootstrapStream.getTarget().toByteArray()).replaceAll("\n", "<br/>");
            //释放空间
            bootstrapStream.target = null;
            bootstrapStream = null;
        }
    }

    //将一般Manager的Logger置为无效
    public static void invalidInitLogger(ManagerType managerType)
    {
        AteyeOutputStream ateyeOutputStream = streamMap.remove(managerType);
        if(ateyeOutputStream!=null)
        {
            ateyeOutputStream.invalid();
            initLogs.put(managerType, new String(ateyeOutputStream.getTarget().toByteArray()).replaceAll("\n", "<br/>"));
            //释放空间
            ateyeOutputStream.target = null;
        }
    }

    //将Throwable打印为String
    public static String convertThrowableToString(Throwable t)
    {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        PrintWriter out=new PrintWriter(bo);
        t.printStackTrace(out);
        out.close();
        return new String(bo.toByteArray()).replaceAll("\n", "<br/>");
    }
}
