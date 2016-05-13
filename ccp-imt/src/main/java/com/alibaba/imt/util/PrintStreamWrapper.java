package com.alibaba.imt.util;

import java.io.PrintStream;

public class PrintStreamWrapper extends PrintStream
{
    public final AteyeOutputStream ao;
    public PrintStreamWrapper(AteyeOutputStream out, boolean autoFlush)
    {
        super(out, autoFlush);
        ao = out;
    }
}
