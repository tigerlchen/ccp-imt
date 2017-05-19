/**
 * Project: imt
 *
 * File Created at 2012-9-18
 * $Id: Interface.java 471736 2013-03-06 08:55:11Z admin.for.perth $
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
package org.ccp.imt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 逍冲
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interface {
	/**
	 * @Interface(datas = { 
	 * 		"data mi test",    		方法描述
	 * 		"product",				是否线上
	 * 		"Data Migration",   	分组一
	 * 		"correct trade data",	分组二
	 * 		"param1" 				第一个参数
	 * 		"param2"				第二个参数
	 * 		...						参数可以无限个						
	 * 	})
	 *
	 *  老注解方式
	 */
	String[] datas();
}
