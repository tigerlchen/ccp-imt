/**
 * Project: imt
 *
 * File Created at 2012-9-27
 * $Id: Test.java 471736 2013-03-06 08:55:11Z admin.for.perth $
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.imt;

import com.alibaba.imt.annotation.Interface;

/**
 * TODO Comment of Test
 * @author yansong.baiys
 *
 */
public class Test {

    @Interface(datas = { "测试方法1", "product", "provider", "group1"})
    public void testMethod1(int i){
        System.out.println("this is testMethod " + i);
    }
    @Interface(datas = { "测试方法2", "product", "provider2", "group2"})
    public String testMethod2(int i, String s){
        System.out.println("this is testMethod " + i + "the string is " + s);
        return s;
    }

    //@Imt()
    public String testMethod3(int i) {
        return "";
    }

    public static void main(String[] args){

        String[] aa = new String[1];

        System.out.println(aa.getClass().getClass());

    }
}
