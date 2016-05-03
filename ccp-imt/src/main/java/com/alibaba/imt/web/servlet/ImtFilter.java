package com.alibaba.imt.web.servlet;

import static com.alibaba.imt.web.ImtPageGen.process;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.imt.web.ImtWebContext;

/**
 * IMT WEB 统一入口
 * @author hongwei.quhw
 *
 */
public class ImtFilter implements Filter{
	//配置了spring容器，且配置了特殊的容器名字时，需要把容器名字注入
	private String contextAttribute;
	private ServletContext servletContext;
	private String encoding;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		process(new ImtWebContext(
			(HttpServletRequest)req, 
			(HttpServletResponse)resp,
			((HttpServletRequest)req).getRequestURL().toString(), 
			contextAttribute, 
			encoding, 
			servletContext, 
			req.getParameterValues("arg"), 
			req.getParameterValues("additionalData"), 
			req.getParameter("key"),
			req.getParameter("uuid")
		));
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		servletContext = filterConfig.getServletContext();
	}
	
	@Override
	public void destroy() {
	}
	public String getContextAttribute() {
		return contextAttribute;
	}
	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
