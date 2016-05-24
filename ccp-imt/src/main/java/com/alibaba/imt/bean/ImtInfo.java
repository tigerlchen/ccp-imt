package com.alibaba.imt.bean;

public class ImtInfo {
    private String mehtodDescrption;
    
    private String env;
    
    private String[] group;
    
    private String[] paramDescrption;

    public String getMehtodDescrption() {
        return mehtodDescrption;
    }

    public void setMehtodDescrption(String mehtodDescrption) {
        this.mehtodDescrption = mehtodDescrption;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String[] getGroup() {
        return group;
    }

    public void setGroup(String[] group) {
        this.group = group;
    }

    public String[] getParamDescrption() {
        return paramDescrption;
    }

    public void setParamDescrption(String[] paramDescrption) {
        this.paramDescrption = paramDescrption;
    }

}
