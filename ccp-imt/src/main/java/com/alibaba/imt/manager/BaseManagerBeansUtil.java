package com.alibaba.imt.manager;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.imt.util.ManagerLoggerUtil;

public class BaseManagerBeansUtil 
{
    /**
     * 获取webx2所管理的beans
     * @param webx2Config
     * @return
     * 
     * 反射前的源码如下：
     * ServiceManager serviceManager = SingletonServiceManagerLoader.getInstance("/classpath/xxx-biz-service.xml");   // 应用Spring总配置
     * BeanFactoryService beanFactoryService = (BeanFactoryService)serviceManager.getService(BeanFactoryService.SERVICE_NAME);
     * String[] beanNames = beanFactoryService.getBeanFactory().getBeanDefinitionNames();
     * for(int j = 0; j < beanNames.length; j++){
     * try{
     * Object obj = beanFactoryService.getBean(beanNames[j]);
     * if(obj != null){
     *      // do something ，such as deal special beans
     * }
     * }catch(Exception e){
     * e.printStackTrace();
     * }
     * }
     * 
     */
    public static Map<String/*beanName*/, Object/*bean*/> getAllBeansFromWebx2(String webx2Config,PrintStream info)
    {
        Map<String, Object> beans =new HashMap<String, Object>();
            try 
            {
                Class<?> serviceManagerClass=Class.forName("com.alibaba.service.ServiceManager");
                Class<?> singletonServiceManagerLoaderClass=Class.forName("com.alibaba.service.context.SingletonServiceManagerLoader");
                Class<?> beanFactoryServiceClass=Class.forName("com.alibaba.service.spring.BeanFactoryService");
                Class<?> classUtilClass=Class.forName("com.alibaba.common.lang.ClassUtil");
                
                Method getInstanceMethod=singletonServiceManagerLoaderClass.getMethod("getInstance", new Class[]{String.class});
                Object serviceManager=getInstanceMethod.invoke(null, webx2Config);
                if(serviceManagerClass.isInstance(serviceManager))
                {
                     Method getServiceMethod=serviceManagerClass.getMethod("getService", new Class[]{String.class});
                     Method getShortClassNameMethod=classUtilClass.getMethod("getShortClassName", new Class[]{Class.class});
                     Object shortName=getShortClassNameMethod.invoke(null, beanFactoryServiceClass);
                     Object beanFactoryService=getServiceMethod.invoke(serviceManager, new Object[]{shortName});
                     if(beanFactoryServiceClass.isInstance(beanFactoryService))
                     {
                         Method getBeanFactoryMethod=beanFactoryServiceClass.getMethod("getBeanFactory", (Class<?>[])null);
                         Object beanFactory=getBeanFactoryMethod.invoke(beanFactoryService, (Object[])null);
                         if(ApplicationContext.class.isInstance(beanFactory))
                         {
                             Method getBeanMethod=beanFactoryServiceClass.getMethod("getBean", new Class[]{String.class});
                             ApplicationContext myBeanFactory=(ApplicationContext) beanFactory;
                             String[] beanNames = myBeanFactory.getBeanDefinitionNames();
                             for(int j = 0; j < beanNames.length; j++)
                             {
                                 try
                                 {
                                     Object bean=getBeanMethod.invoke(beanFactoryService, beanNames[j]);
                                     beans.put(beanNames[j], bean);
                                 }
                                 catch (Exception e) 
                                 {
                                    info.println("从Webx2的Bean容器获得bean异常，beanName:"+beanNames[j]+" 异常:"+ManagerLoggerUtil.convertThrowableToString(e));
                                }
                             }
                         }
                     }
                 }
            } 
            catch (Exception e)
            {
                info.println("访问Webx2的Bean容器异常:"+ManagerLoggerUtil.convertThrowableToString(e));
            }
            return beans;
    }
    
    public static Map<String/*beanName*/, Object/*bean*/> getAllBeans(ServletContext servletContext, PrintStream info)
    {
        Map<String, Object> beans =new HashMap<String, Object>();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) 
        {
            try 
            {
                Object bean = applicationContext.getBean(beanName);
                if(bean!=null)
                {
                    beans.put(beanName, bean);
                }
            } 
            catch (Exception e) 
            {
                info.println("Exception when spring gets bean of name: "+beanName);
            }
        }
        return beans;
    }
}
