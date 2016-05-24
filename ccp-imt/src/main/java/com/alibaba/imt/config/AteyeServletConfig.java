//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.imt.config;

import com.alibaba.imt.annotation.Switch;

public class AteyeServletConfig {
    public static Boolean isRecordErrorLogger;
    public static Boolean isRecordLog4j;
    public static String appName;
    @Switch(
            description = "是否记录sql埋点统计"
    )
    public static Boolean isRecordSqlStat;
    @Switch(
            description = "是否打开内存监控"
    )
    public static Boolean isOpenMemoryWatch;
    @Switch(
            description = "估算内存用量时的采样个数"
    )
    public static Integer memSampleNumbers;
    @Switch(
            description = "KV埋点采集间隔(单位s)"
    )
    private static Long kvAwaitTime;
    @Switch(
            description = "是否打开KV详情埋点(慎重,量大)"
    )
    public static Boolean isOpenKVDetail;

    public AteyeServletConfig() {
    }

    public Boolean getIsRecordErrorLogger() {
        return isRecordErrorLogger;
    }

    public void setIsRecordErrorLogger(Boolean isRecordErrorLogger) {
        isRecordErrorLogger = isRecordErrorLogger;
    }

    public Boolean getIsRecordSqlStat() {
        return isRecordSqlStat;
    }

    public void setIsRecordSqlStat(Boolean isRecordSqlStat) {
        isRecordSqlStat = isRecordSqlStat;
    }

    public void setIsOpenMemoryWatch(Boolean isOpenMemoryWatch) {
        isOpenMemoryWatch = isOpenMemoryWatch;
    }

    public Boolean getIsOpenMemoryWatch() {
        return isOpenMemoryWatch;
    }

    public void setMemSampleNumbers(Integer memSampleNumbers) {
        memSampleNumbers = memSampleNumbers;
    }

    public Integer getMemSampleNumbers() {
        return memSampleNumbers;
    }

    public static Long getKvAwaitTime() {
        return kvAwaitTime;
    }

    public static void setKvAwaitTime(Long kv) {
        if(kv == null || kv.longValue() < 60L) {
            kv = Long.valueOf(60L);
        }

        kvAwaitTime = kv;
    }

    public void setIsOpenKVDetail(Boolean isOpenKVDetail) {
        isOpenKVDetail = isOpenKVDetail;
    }

    public Boolean getIsOpenKVDetail() {
        return isOpenKVDetail;
    }

    public Boolean getIsRecordLog4j() {
        return isRecordLog4j;
    }

    public void setIsRecordLog4j(Boolean isRecordLog4j) {
        isRecordLog4j = isRecordLog4j;
    }

    static {
        isRecordErrorLogger = Boolean.TRUE;
        isRecordLog4j = Boolean.TRUE;
        appName = "";
        isRecordSqlStat = Boolean.TRUE;
        isOpenMemoryWatch = Boolean.TRUE;
        memSampleNumbers = Integer.valueOf(10000);
        kvAwaitTime = Long.valueOf(60L);
        isOpenKVDetail = Boolean.valueOf(false);
    }
}
