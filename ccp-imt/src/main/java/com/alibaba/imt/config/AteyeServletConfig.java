package com.alibaba.imt.config;

import com.alibaba.imt.annotation.Switch;

/**
 * 用于控制AteyeServlet端行为的bean
 * 有些参数在运行期无法修改，则不设置为开关
 * 
 * 
 * @author leconte
 *
 */
public class AteyeServletConfig {
    /*参数*/
    public static Boolean isRecordErrorLogger = Boolean.TRUE;//是否记录错误日志
    public static Boolean isRecordLog4j = Boolean.TRUE;//记录log4j
    public static String appName = "";
    @Switch(description="是否记录sql埋点统计")
    public static Boolean isRecordSqlStat = Boolean.TRUE;//是否记录sql日志埋点
    @Switch(description="是否打开内存监控")
    public static Boolean isOpenMemoryWatch = Boolean.TRUE;//是否打开内存监控
    @Switch(description="估算内存用量时的采样个数")
    public static Integer memSampleNumbers = 10000;//估算内存用量时的采样个数
    @Switch(description="KV埋点采集间隔(单位s)")
    private static Long kvAwaitTime = 60l;//KV埋点时候多久写一次日志，默认1分钟
    @Switch(description="是否打开KV详情埋点(慎重,量大)")
    public static Boolean isOpenKVDetail=false;//是否打开KV详情埋点

    
    public Boolean getIsRecordErrorLogger() {
        return isRecordErrorLogger;
    }
    public void setIsRecordErrorLogger(Boolean isRecordErrorLogger) {
        AteyeServletConfig.isRecordErrorLogger = isRecordErrorLogger;
    }
    public Boolean getIsRecordSqlStat() {
        return isRecordSqlStat;
    }
    public void setIsRecordSqlStat(Boolean isRecordSqlStat) {
        AteyeServletConfig.isRecordSqlStat = isRecordSqlStat;
    }
    public void setIsOpenMemoryWatch(Boolean isOpenMemoryWatch) {
        AteyeServletConfig.isOpenMemoryWatch = isOpenMemoryWatch;
    }
    public Boolean getIsOpenMemoryWatch() {
        return isOpenMemoryWatch;
    }
    public void setMemSampleNumbers(Integer memSampleNumbers) {
        AteyeServletConfig.memSampleNumbers = memSampleNumbers;
    }
    public Integer getMemSampleNumbers() {
        return memSampleNumbers;
    }
    public static Long getKvAwaitTime() {
        return kvAwaitTime;
    }
    public static void setKvAwaitTime(Long kv) {
        if ( kv == null || kv < 60l ){
            kv = 60l;
        }
        AteyeServletConfig.kvAwaitTime = kv ;
    }
    public void setIsOpenKVDetail(Boolean isOpenKVDetail) {
        AteyeServletConfig.isOpenKVDetail = isOpenKVDetail;
    }
    public Boolean getIsOpenKVDetail() {
        return isOpenKVDetail;
    }
    public Boolean getIsRecordLog4j() {
        return isRecordLog4j;
    }
    public void setIsRecordLog4j(Boolean isRecordLog4j) {
        AteyeServletConfig.isRecordLog4j = isRecordLog4j;
    }
}

