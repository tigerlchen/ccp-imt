package org.ccp.imt.annotation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 逍冲 [2017年05月18日 下午4:08]
 */
public class ImtMethod {

    private Long id;

    private Imt imt;

    private Method method;

    private Object bean;

    private String methodName;

    private String beanName;

    private List<Param> params;

    private Integer paramSize;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Imt getImt() {
        return imt;
    }

    public void setImt(Imt imt) {
        this.imt = imt;
    }

    public Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getParamSize() {
        return paramSize;
    }

    public void setParamSize(Integer paramSize) {
        this.paramSize = paramSize;
    }

    public static class Param {

        private Long id;

        private String paramName;

        private Class<?> paramClass;

        private String paramClassName;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Class<?> getParamClass() {
            return paramClass;
        }

        public void setParamClass(Class<?> paramClass) {
            this.paramClass = paramClass;
        }

        public String getParamClassName() {
            return paramClassName;
        }

        public void setParamClassName(String paramClassName) {
            this.paramClassName = paramClassName;
        }
    }
}
