package com.alibaba.imt.excaption;

/*
 * Ateye初始化异常，发生此异常将导致应用无法启动
 */
public class AteyeInitException extends RuntimeException{

    private static final long serialVersionUID = 1798008724023647062L;
    
    public AteyeInitException(String message) {
        super(message);
    }


    public AteyeInitException(String message, Throwable cause) {
        super(message, cause);
    }
    public AteyeInitException(Throwable cause) {
        super(cause);
    }
}
