package com.alibaba.imt.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AteyePrintStreamFactory
{
    //Ateye.out输出的默认大小为1M
    public static int _ATEYE_OUT_LIMIT = 1024*1024;

    public static final ThreadLocal<PrintStreamWrapper> printStreamLocal = new ThreadLocal<PrintStreamWrapper>();

    public static PrintStream getPrintStream()
    {
        return printStreamLocal.get();
    }

    //由Ateye的Invoker代码负责调用
    public static void put()
    {
        AteyeOutputStream ao = new AteyeOutputStream(new ByteArrayOutputStream(), _ATEYE_OUT_LIMIT);
        PrintStreamWrapper psw = new PrintStreamWrapper(ao, true);
        printStreamLocal.set(psw);
    }

    //由Ateye的Invoker代码负责调用
    public static String getAteyeOut()
    {
        String outMessage = "";
        PrintStreamWrapper psw = printStreamLocal.get();
        if(psw!=null)
        {
            psw.flush();
            AteyeOutputStream ao = psw.ao;
            outMessage = new String(ao.getTarget().toByteArray()).replaceAll("\n", "<br/>");
            printStreamLocal.remove();
        }
        return outMessage;
    }
}