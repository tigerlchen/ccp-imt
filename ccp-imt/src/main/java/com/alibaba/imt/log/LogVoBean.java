package com.alibaba.imt.log;

public class LogVoBean{
    /**
     * logger的name
     */
    private String name;
    /**
     * logger的level
     */
    private String level;
    /**
     * 判断是log4j还是logback的logger
     */
    private String type;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
