package com.alibaba.imt.adapter.privileges;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认实现权限接口
 * @author 逍冲 [2015年5月16日 下午6:01:24]
 */
public class AbstractImtPrivilege implements ImtPrivilege{

	@Override
	public boolean authUser() {
		return false;
	}

	@Override
	public boolean authUser(HttpServletRequest request) {
		return false;
	}

}
