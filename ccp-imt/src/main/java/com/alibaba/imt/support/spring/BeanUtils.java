package com.alibaba.imt.support.spring;

import static com.alibaba.imt.util.StringUtil.trimToNull;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class BeanUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getBeanByType(Class<T> clazz, ApplicationContext context) {
		Assert.notNull(context);
		Assert.notNull(clazz);
		
		//Spring3.0后才支持 <T> T getBean(Class<T> requiredType) throws BeansException;
		//为了兼容以前版本，故如下
		String[] names = context.getBeanNamesForType(clazz);
		
		if (null == names || names.length == 0) {
			return null;
		}
		
		List<T> beans = new ArrayList<T>();
		
		for (String name : names) {
			beans.add((T)context.getBean(name));
		}
		
		return beans;
	}

	public static WebApplicationContext findWebApplicationContext(ServletContext sc, String contextAttribute) {
		WebApplicationContext webApplicationContext = null;
		contextAttribute = trimToNull(contextAttribute);
		
		if (null == contextAttribute) {
			webApplicationContext = getWebApplicationContext(sc);
		} else {
			webApplicationContext = getWebApplicationContext(sc, contextAttribute);
		}
		
		return webApplicationContext;
	} 
}
