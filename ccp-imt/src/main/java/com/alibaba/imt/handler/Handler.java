/**
 * Project: imt
 * 
 * File Created at 2012-9-17
 * $Id: Handler.java 467468 2012-10-26 06:11:41Z admin.for.perth $
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
package com.alibaba.imt.handler;

import java.util.List;
import java.util.Map;

public interface Handler {

    void handleClass(String className, List<Map<String, Object>> dataList);
    
}
