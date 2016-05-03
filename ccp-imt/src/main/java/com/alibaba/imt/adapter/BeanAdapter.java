/**
 * Project: imt
 * 
 * File Created at 2012-9-26
 * $Id: BeanAdapter.java 3 2012-11-14 09:20:46Z yourxiaobai@gmail.com $
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
package com.alibaba.imt.adapter;


/**
 * @author yansong.baiys
 *
 */
public interface BeanAdapter {

    Object getObject(Class<?> clazz, Object[] beanDatas);
    
    Object[] getBeanDatas(Class<?> clazz);
}
