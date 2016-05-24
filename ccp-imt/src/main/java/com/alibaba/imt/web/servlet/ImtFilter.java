package com.alibaba.imt.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.imt.bean.ImtGroup;
import com.alibaba.imt.constants.ManagerType;
import com.alibaba.imt.manager.AteyeManager;
import com.alibaba.imt.manager.AteyeManagerContext;
import com.alibaba.imt.util.ResourceUtil.ImtResourceLoader;

/**
 * IMT WEB 统一入口
 *
 * @author hongwei.quhw
 */
public class ImtFilter implements Filter {

    private static Logger       logger           = Logger.getLogger("ateyeClient");

    private static final String DEFAULT_ENCODING = "GBK";
    private String              encoding;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        response.setCharacterEncoding("gb2312");
        response.setContentType("application/json;charset=gb2312");
        PrintWriter out = response.getWriter();
        AteyeManager manager = AteyeManagerContext.INSTANCE.getManagerMap().get(ManagerType.INVOKER);
        if (manager != null) {
            Map<String, String> paramMap = convertParamMap(request.getParameterMap());
            paramMap.put("action", "list");
            String json = manager.service(paramMap);
            JSONArray methods = JSON.parseArray(json);

            VelocityContext imtWebContext = new VelocityContext();
            imtWebContext.put("url", ((HttpServletRequest) request).getRequestURL().toString());
            imtWebContext.put("encoding", DEFAULT_ENCODING);

            ImtGroup group = new ImtGroup("test");
            imtWebContext.put("group", group);

            List<ImtGroup> goups = new ArrayList<ImtGroup>();
            goups.add(group);
            imtWebContext.put("groups", goups);

            out.println(merge(imtWebContext, "/vm/page.vm"));
        } else {
            logger.error("未找到针对请求类型:InvokerManager");
        }
    }

    public static String merge(VelocityContext context, String path) {
        try {
            VelocityEngine ve = new VelocityEngine();
            ve.setProperty("resource.loader", "imt");
            ve.setProperty("imt.resource.loader.class", ImtResourceLoader.class.getName());
            ve.setProperty("input.encoding", "UTF-8");
            ve.setProperty("output.encoding", DEFAULT_ENCODING);
            ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
            ve.init();

            Template template = ve.getTemplate(path, DEFAULT_ENCODING);

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("渲染模版出错,", e);
        }
    }

    private Map<String, String> convertParamMap(Map<String, String[]> oriQueryMap) {
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : oriQueryMap.entrySet()) {
            if (entry.getValue().length > 0) {
                try {
                    /*
                     * 自版本1.2.0-SNAPSHOT后，ateyeclient会对输入参数额外进行一次decode（应用服务器会decode一次），而ateye平台会在调用时对参数进行两次encode
                     * 用这种方法来消除中文编码不同造成的乱码问题
                     */
                    map.put(entry.getKey(), URLDecoder.decode(entry.getValue()[0], "GBK"));
                } catch (Exception e) {
                    logger.error(e);
                }
            } else {
                map.put(entry.getKey(), "");
            }
        }
        return map;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
