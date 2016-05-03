package com.alibaba.imt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alibaba.imt.util.ImtConstant;

/**
 * Imt注解兼容老的interface形式
 * @author 逍冲
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Imt {
	/**
	 * 方法描述
	 * @return
	 */
	String mehtodDescrption();
	
	/**
	 * 适用环境开发，测试，线上
	 * @return
	 */
	String env() default ImtConstant.ENV_PRODUCT;
	
	/**
	 * 分组
	 * @return
	 */
	String[] group() default {};
	
	/**
	 * 方法参数描述
	 * @return
	 */
	String[] paramDescrption() default {};
}
