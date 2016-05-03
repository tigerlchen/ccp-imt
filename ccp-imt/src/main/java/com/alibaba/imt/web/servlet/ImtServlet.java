package com.alibaba.imt.web.servlet;

import static com.alibaba.imt.util.StringUtil.trimToNull;
import static com.alibaba.imt.web.ImtPageGen.process;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.imt.web.ImtWebContext;

/**
 * IMT WEB 统一入口
 * @author hongwei.quhw
 *
 */
public class ImtServlet extends HttpServlet{
    private static final long serialVersionUID = 7504647294332154019L;
    
    //配置了spring容器，且配置了特殊的容器名字时，需要把容器名字注入
	private String contextAttribute;
	private ServletContext servletContext;
	private String encoding;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(new ImtWebContext(
			req,
			resp,
			req.getRequestURL().toString(),
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
	public void init(ServletConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}
	
	@Override
    public final ServletContext getServletContext() {
		return servletContext;
	}
	
	public final String getContextAttribute() {
		return contextAttribute;
	}

	public final void setContextAttribute(String contextAttribute) {
		this.contextAttribute = trimToNull(contextAttribute);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
