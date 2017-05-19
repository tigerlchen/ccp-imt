package org.ccp.imt;

import org.ccp.imt.annotation.Imt;
import org.ccp.imt.annotation.ImtMethod;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * DTS endpoint
 *
 * @author leijuan
 */
// @ConfigurationProperties(prefix = "endpoints.schedulerx", ignoreUnknownFields = false)
public class ImtEndpoint extends AbstractAliExpressEndpoint implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String, Object> runtime;

    private Map<Long, ImtMethod> imtMethodMap;

    public ImtEndpoint() {
        super("imt");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    public String getName() {
        return "imt";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public List<String> getAuthors() {
        return Collections.singletonList("雷卷 <jacky.chenlb@alibaba-inc.com>");
    }

    public String getDocs() {
        return "http://gitlab.alibaba-inc.com/spring-boot/schedulerx-spring-boot-starter/tree/master";
    }

    public String getScm() {
        return "git@gitlab.alibaba-inc.com:spring-boot/schedulerx-spring-boot-starter.git";
    }

    @Override
    public Optional<Object> getConfig() {
        return Optional.empty();
    }

    @Override
    public Optional<Map<String, Object>> getRuntime() {
        if (runtime == null) {
            runtime = new HashMap();
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                Method[] methods = applicationContext.getType(beanName).getMethods();
                for (Method method : methods) {
                    ImtMethod imtMethod = new ImtMethod();

                    Imt imt = AnnotationUtils.findAnnotation(method, Imt.class);
                    if (imt != null) {
                        runtime.put(beanName, imt);
                    }
                }
            }
        }
        return Optional.of(runtime);
    }

    public synchronized Map<Long, ImtMethod> getImtMethod() {
        if (imtMethodMap == null) {
            imtMethodMap = new HashMap();
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            Long i = 1L;
            for (String beanName : beanNames) {
                Method[] methods = applicationContext.getType(beanName).getMethods();
                for (Method method : methods) {
                    ImtMethod imtMethod = new ImtMethod();

                    Imt imt = AnnotationUtils.findAnnotation(method, Imt.class);
                    if (imt != null) {
                        imtMethod.setImt(imt);
                        imtMethod.setMethod(method);
                        imtMethod.setMethodName(method.getName());
                        imtMethod.setBeanName(beanName);
                        imtMethod.setBean(applicationContext.getBean(beanName));

                        List<ImtMethod.Param> params = new ArrayList<>();
                        Long j = 1L;
                        for (Parameter param : method.getParameters()) {
                            ImtMethod.Param imtParam = new ImtMethod.Param();
                            imtParam.setParamName(param.getName());
                            imtParam.setParamClassName(param.getType().getSimpleName());
                            imtParam.setParamClass(param.getType());
                            imtParam.setId(j);

                            params.add(imtParam);
                            j++;
                        }
                        imtMethod.setParams(params);
                        imtMethod.setId(i);
                        imtMethod.setParamSize(params.size());

                        imtMethodMap.put(i, imtMethod);
                        i++;
                    }
                }
            }
        }
        return imtMethodMap;
    }

}
