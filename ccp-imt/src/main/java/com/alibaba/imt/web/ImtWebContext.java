package com.alibaba.imt.web;

import static com.alibaba.imt.util.StringUtil.trimToNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;

import com.alibaba.imt.InterfaceManagementTool;
import com.alibaba.imt.adapter.privileges.ImtPrivilege;

/**
 * 
 * @author hongwei.quhw
 *
 */
public class ImtWebContext extends VelocityContext implements Serializable{
	private static final String DEFAULT_ENCODING = "GBK";
	private final String url;
	private final String contextAttribute;
	private final ServletContext servletContext;
	private final String[] args;
	private final String[] additionalData;
	private final String key;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final String encoding;
	private final String uuid;
	
	private InterfaceManagementTool interfaceManagementTool;
	private List<ImtPrivilege> imtPrivileges;
	private boolean isAuthed;
	
	public ImtWebContext(HttpServletRequest request, HttpServletResponse response,
			String url, String contextAttribute,
			String encoding, ServletContext servletContext, String[] args,
			String[] additionalData, String key, String uuid) {
		super();
		this.request = request;
		this.response = response;
		this.url = url;
		this.contextAttribute = contextAttribute;
		this.encoding = encoding;
		this.servletContext = servletContext;
		this.args = args;
		this.additionalData = additionalData;
		this.key = key;
		this.uuid = uuid;
	}

	public void render(String content) throws IOException {
		response.getWriter().println(content);
	}
	
	public void setHtmlContentType() {
		response.setContentType("text/html;charset=" + getEncoding());
	}
	
	public void setJavaScriptContentType() {
		response.setContentType("text/javascript;charset=" + getEncoding());
	}
	
	public void setCssContentType() {
		response.setContentType("text/css;charset=" + getEncoding());
	}
	
	public void setJsonContentType() {
		response.setContentType("application/json;charset=" + getEncoding());
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getEncoding() {
		return trimToNull(encoding) == null ? DEFAULT_ENCODING : trimToNull(encoding);
	}

	public InterfaceManagementTool getInterfaceManagementTool() {
		return interfaceManagementTool;
	}

	public void setInterfaceManagementTool(
			InterfaceManagementTool interfaceManagementTool) {
		this.interfaceManagementTool = interfaceManagementTool;
	}

	public List<ImtPrivilege> getImtPrivileges() {
		return imtPrivileges;
	}

	public void setImtPrivileges(List<ImtPrivilege> imtPrivileges) {
		this.imtPrivileges = imtPrivileges;
	}

	public boolean isAuthed() {
		return isAuthed;
	}

	public void setAuthed(boolean isAuthed) {
		this.isAuthed = isAuthed;
	}

	public String getUrl() {
		return url;
	}

	public String getContextAttribute() {
		return contextAttribute;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public String getUuid() {
		return uuid;
	}

	public String[] getArgs() {
		return args;
	}

	public String[] getAdditionalData() {
		return additionalData;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" key=[").append(key).append("]");
		sb.append(", additionalData=[");
		if (null != additionalData) {
			for (String add : additionalData) {
				sb.append(add).append(",");
			}
		}
		sb.append("]");
		sb.append(", args=[");
		if (null != args) {
			for (String arg : args) {
				sb.append(arg).append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
