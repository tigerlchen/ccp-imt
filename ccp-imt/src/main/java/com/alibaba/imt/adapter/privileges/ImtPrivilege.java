package com.alibaba.imt.adapter.privileges;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限回调，用于获取登陆用户
 * 应用方需实现此接口
 * 没有实现imt不做权限控制
 * @author 逍冲
 *
 */
public interface ImtPrivilege {
	
	boolean authUser();
	
	boolean authUser(HttpServletRequest request);
}
