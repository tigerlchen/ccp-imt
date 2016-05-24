package com.alibaba.imt.util;

import static com.alibaba.imt.util.StringUtil.trimToNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import com.alibaba.fastjson.JSON;
import com.alibaba.imt.web.ImtPageGen;
import com.alibaba.imt.web.ImtWebContext;

/**
 * 资源加载工具
 * @author 逍冲
 *
 */
public class ResourceUtil {
	public static boolean isInitPage(ImtWebContext imtWebContext) {
		return !isResource(imtWebContext) && !isContent(imtWebContext) && null == trimToNull(imtWebContext.getKey());
	}

	public static boolean isContent(ImtWebContext imtWebContext) {
		return null != trimToNull(imtWebContext.getUuid());
	}

	public static boolean isMethodInvoke(ImtWebContext imtWebContext) {
		return null != imtWebContext.getKey();
	}

	public static boolean isResource(ImtWebContext imtWebContext) {
		return isCssResource(imtWebContext) || isJsResource(imtWebContext) || isImgResource(imtWebContext);
	}

	public static boolean isCssResource(ImtWebContext imtWebContext) {
		return imtWebContext.getUrl().indexOf("/css") > 0;
	}

	public static boolean isJsResource(ImtWebContext imtWebContext) {
		return imtWebContext.getUrl().indexOf("/js") > 0;
	}

	public static boolean isImgResource(ImtWebContext imtWebContext) {
		return imtWebContext.getUrl().indexOf("/img") > 0;
	}

	public static void renderPage(ImtWebContext imtWebContext) throws IOException {
		imtWebContext.setHtmlContentType();
		imtWebContext.render(merge(imtWebContext, "/vm/page.vm"));
	}

	public static void renderJsResource(ImtWebContext imtWebContext) throws IOException {
		imtWebContext.setJavaScriptContentType();

		int pos = imtWebContext.getUrl().indexOf("/js");
		String path = imtWebContext.getUrl().substring(pos);

		InputStream is = null;
		BufferedReader in = null;
		try {
			is = ImtPageGen.class.getResourceAsStream(path);
			in = new BufferedReader(new InputStreamReader(is));

			String line = "";
			while ((line = in.readLine()) != null){
				imtWebContext.render(line);
			}
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void renderMethodInvoke(ImtWebContext imtWebContext) throws IOException {
		imtWebContext.setHtmlContentType();
		Object ret = imtWebContext.getInterfaceManagementTool().invoke(imtWebContext.getKey(), imtWebContext.getAdditionalData(), imtWebContext.getArgs());
		imtWebContext.render(JSON.toJSONString(ret));
	}

	public static void renderImgResource(ImtWebContext imtWebContext) throws IOException {
		int pos = imtWebContext.getUrl().indexOf("/img");
		String path = imtWebContext.getUrl().substring(pos);

		InputStream is = null;
		try {
			is = ImtPageGen.class.getResourceAsStream(path);
			byte[] b = new byte[4000];
			is.read(b);

			imtWebContext.getResponse().getOutputStream().write(b);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void renderCssResource(ImtWebContext context) throws IOException {
		context.setCssContentType();

		int pos = context.getUrl().indexOf("/css");
		String path = context.getUrl().substring(pos);
		context.put("url", context.getUrl().substring(0, pos));
		context.put("encoding", context.getEncoding());

		context.render(merge(context, path));
	}

	public static String merge(ImtWebContext context, String path) {
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "imt");
			ve.setProperty("imt.resource.loader.class", ImtResourceLoader.class.getName());
			ve.setProperty("input.encoding", "UTF-8");
			ve.setProperty("output.encoding", context.getEncoding());
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
			ve.init();

			Template template = ve.getTemplate(path, context.getEncoding());

			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException("渲染模版出错," , e);
		}
	}

	public static class ImtResourceLoader extends ResourceLoader {

		@Override
		public void init(ExtendedProperties configuration) {
		}

		@Override
		public InputStream getResourceStream(String source) throws ResourceNotFoundException {
			return getClass().getResourceAsStream(source);
		}

		@Override
		public boolean isSourceModified(Resource resource) {
			return false;
		}

		@Override
		public long getLastModified(Resource resource) {
			return 0;
		}
	}
}
