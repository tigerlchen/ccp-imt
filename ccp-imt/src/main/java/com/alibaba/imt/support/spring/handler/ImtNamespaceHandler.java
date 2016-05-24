package com.alibaba.imt.support.spring.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.alibaba.imt.InterfaceManagementTool;
import com.alibaba.imt.asm.Type;
import com.alibaba.imt.support.spring.adapter.SpringBeanAdapter;
import com.alibaba.imt.support.spring.scanner.SpringClassScanner;
import com.alibaba.imt.util.Util;

/**
 * {@link NamespaceHandler} for the <code>imt</code> namespace.
 *
 * @author yansong.baiys
 */
public class ImtNamespaceHandler extends NamespaceHandlerSupport {

    private static final String PATHS_ELEMENT = "annotation-scan-paths";
    private static final String CLASS_ELEMENT = "interface-class";
    private static final String PATH_ELEMENT = "path";
    private static final String DATA_ELEMENT = "data";
    private static final String INTERFACE_ELEMENT = "interface";
    private static final String CLASS_NAME_ATTR = "className";
    private static final String DESCRIPTION_ATTR = "description";
    private static final String PRIVILEGE_ELEMENT = "privilege";


    public void init() {
        registerBeanDefinitionParser("imt", new ImtBeanDefinitionParser());
    }


    private static class ImtBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

        private boolean annotationScan = false;
        @Override
        protected Class<?> getBeanClass(Element element) {
            return InterfaceManagementTool.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            return id;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            super.doParse(element, parserContext, builder);
            Set<String> paths = new HashSet<String>();
            List<Map<String,Object>> xmlDataList = new ArrayList<Map<String,Object>>();
            String privilegeBean = null;
		    /*List<Element> childElts = DomUtils.getChildElements(element);
	        for (Element elt: childElts) {
	            String localName = parserContext.getDelegate().getLocalName(elt);
	            if (PATHS_ELEMENT.equals(localName)) {
	                annotationScan = true;
	                parsePathsElement(elt, parserContext, paths);
	            } else if (CLASS_ELEMENT.equals(localName)) {
	                parseClassElement(elt, parserContext, xmlDataList);
	            } else if (PRIVILEGE_ELEMENT.equals(localName)) {
	                privilegeBean = elt.getAttribute("ref");
	            }
	        }*/
            Element pathsElement = DomUtils.getChildElementByTagName(element, PATHS_ELEMENT);
            if(pathsElement != null){
                parsePathsElement(pathsElement, parserContext, paths);
            }
            Element classElement = DomUtils.getChildElementByTagName(element, CLASS_ELEMENT);
            if(classElement != null){
                parseClassElement(classElement, parserContext, xmlDataList);
            }
            Element privilegeElement = DomUtils.getChildElementByTagName(element, PRIVILEGE_ELEMENT);
            if(privilegeElement != null){
                privilegeBean = privilegeElement.getAttribute("ref");
                builder.addPropertyReference("imtPrivilege", privilegeBean);
            }

            //将SpringBeanAdapter放到spring 容器中
            GenericBeanDefinition springBeanAdapterDB = new GenericBeanDefinition();
            springBeanAdapterDB.setBeanClass(SpringBeanAdapter.class);
            parserContext.getRegistry().registerBeanDefinition("springBeanAdapterDB", springBeanAdapterDB);

            builder.addPropertyValue("annotationScan", annotationScan);
            builder.addPropertyValue("paths", paths);
            builder.addPropertyValue("xmlDataList", xmlDataList);
            builder.addPropertyReference("beanAdapter", "springBeanAdapterDB");
            builder.addPropertyValue("scanner", new SpringClassScanner());
            builder.setInitMethodName("init");


        }

        /**
         * @param element
         * @param parserContext
         * @param xmlDataList
         */
        private void parseClassElement(Element element, ParserContext parserContext, List<Map<String, Object>> xmlDataList) {
            String className = element.getAttribute(CLASS_NAME_ATTR).replaceAll("//s+", "");
            if(StringUtils.hasText(className)){
                List<Element> interfaceElts = DomUtils.getChildElementsByTagName(element, INTERFACE_ELEMENT);
                for (Element interfaceElt : interfaceElts) {
                    String description = interfaceElt.getAttribute(DESCRIPTION_ATTR).replaceAll("//s+", "");
                    if(StringUtils.hasText(description)){
                        List<Element> dataElts = DomUtils.getChildElementsByTagName(interfaceElt, DATA_ELEMENT);
                        int dataEltsSize = dataElts.size();
                        String[] datas = new String[dataEltsSize];
                        for (int i = 0; i < dataEltsSize; i++) {
                            String value = DomUtils.getTextValue(dataElts.get(i)).trim();
                            datas[i] = value;
                        }
                        Object[] wrappedDatas = wrapSingleArray(datas);
                        HashMap<String, Object> dataMap = new HashMap<String, Object>();
                        Class<?> clazz = null;
                        try {
                            clazz = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        String internalClassName = Type.getInternalName(clazz);

                        String methodName = Util.getMethodNameFromMethodExternalDesc(description);
                        Class<?>[] parameterTypes = Util.getParameterTypesFromMethodExternalDesc(description);

                        Method method;
                        try {
                            method = clazz.getMethod(methodName, parameterTypes);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        Object[] wrappedParameterTypes = wrapSingleArray(parameterTypes);
                        String methodDesc = Type.getMethodDescriptor(method);
                        Type returnType = Type.getReturnType(methodDesc);
                        Class<?> returnClass = Util.getClass(returnType);

                        dataMap.put("key", className + "." + description);
                        dataMap.put("internalClassName", internalClassName);
                        dataMap.put("className", className);
                        dataMap.put("clazz", clazz);
                        dataMap.put("methodName", methodName);
                        dataMap.put("methodDesc", methodDesc);
                        dataMap.put("returnClass", returnClass);
                        dataMap.put("argumentClasses", wrappedParameterTypes);
                        dataMap.put("datas", wrappedDatas);
                        xmlDataList.add(dataMap);
                    }
                }
            }
        }

        /**
         * @param datas
         * @return
         */
        private Object[] wrapSingleArray(Object[] objs) {
            //由于spring的TypeConverterDelegate类中convertIfNecessary方法中会将长度为1的数组转为单个对象，这影响到了我们接下来mapToBean的反射，所以这里要多包一层数组
            if(objs.length == 1){
                Object[] wrapObj = new Object[1];
                wrapObj[0] = objs;
                return wrapObj;
            }
            return objs;
        }

        /**
         * @param elt
         * @param parserContext
         * @param pkgs
         */
        private void parsePathsElement(Element element, ParserContext parserContext, Set<String> pkgs) {
            List<Element> pkgElts = DomUtils.getChildElementsByTagName(element, PATH_ELEMENT);
            for (Element pkgElt : pkgElts) {
                String value = DomUtils.getTextValue(pkgElt).trim();
                pkgs.add(value);
            }

        }
    }

}
