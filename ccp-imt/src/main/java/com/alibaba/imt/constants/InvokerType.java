package com.alibaba.imt.constants;

public enum InvokerType
{
    /**
     * 需要只读权限才能访问；
     */
    READ_ONLY,
    /**
     * 需要读写权限才能访问；
     */
    READ_WRITE,
    /**
     * 只在daily环境下可访问，需要只读权限；
     */
    DAILY_READ,
    /**
     * 只在daily环境下可访问，需要读写权限；
     */
    DAILY_WRITE
}

