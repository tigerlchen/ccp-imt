package com.alibaba.imt.manager.impl;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import com.alibaba.imt.annotation.Imt;
import com.alibaba.imt.constants.InvokerType;
import com.alibaba.imt.constants.ManagerType;
import com.alibaba.imt.manager.AteyeManager;
import com.alibaba.imt.util.AteyePrintStreamFactory;
import com.alibaba.imt.util.ManagerLoggerUtil;

public class InvokerManager  implements AteyeManager
{
    private static Logger logger = Logger.getLogger("ateyeClient");
    private PrintStream info = null;
    private Map<String, Object> beans=new HashMap<String, Object>();

    private static Map<Class<?>, Class<?>> typeConverter = new HashMap<Class<?>, Class<?>>();
    static{
        typeConverter.put(boolean.class, Boolean.class);
        typeConverter.put(Boolean.class, Boolean.class);
        typeConverter.put(byte.class, Byte.class);
        typeConverter.put(Byte.class, Byte.class);
        typeConverter.put(short.class, Short.class);
        typeConverter.put(Short.class, Short.class);
        typeConverter.put(char.class, Character.class);
        typeConverter.put(Character.class, Character.class);
        typeConverter.put(int.class, Integer.class);
        typeConverter.put(Integer.class, Integer.class);
        typeConverter.put(long.class, Long.class);
        typeConverter.put(Long.class, Long.class);
        typeConverter.put(float.class, Float.class);
        typeConverter.put(Float.class, Float.class);
        typeConverter.put(double.class, Double.class);
        typeConverter.put(Double.class, Double.class);

        typeConverter.put(String.class, String.class);
        typeConverter.put(Date.class, Date.class);
    }

    private static Set<Class<?>> fundamentalType = new HashSet<Class<?>>();
    static{
        fundamentalType.add(boolean.class);
        fundamentalType.add(byte.class);
        fundamentalType.add(short.class);
        fundamentalType.add(char.class);
        fundamentalType.add(int.class);
        fundamentalType.add(long.class);
        fundamentalType.add(float.class);
        fundamentalType.add(double.class);
    }

    Map<String/*BeanName*/,Map<String/*MethodSignature*/,MethodInfo>> invokers=new HashMap<String/*BeanName*/,Map<String/*MethodSignature*/,MethodInfo>>();
    @Override
    public String service(Map<String, String> queryParams)
    {
        String action = queryParams.get("action");
        if ("list".equalsIgnoreCase(action))
        {
            JSONArray methods = queryAllInvokersInfo();
            return methods.toString();
        }
        else if("single".equalsIgnoreCase(action))
        {
            String beanName = queryParams.get("beanName");
            String signature = queryParams.get("signature");
            JSONObject method = querySingleInvokerInfo(beanName,signature);
            return method.toString();
        }
        else if ("invoke".equalsIgnoreCase(action))
        {
            JSONArray array = new JSONArray();
            JSONObject json = new JSONObject();
            JSONObject method = null;
            String beanName = queryParams.get("beanName");
            String signature = queryParams.get("signature");
            if (this.invokers.get(beanName) != null)
            {
                if (this.invokers.get(beanName).get(signature) != null)
                {
                    method = querySingleInvokerInfo(beanName,signature);
                    MethodInfo mi = this.invokers.get(beanName).get(signature);
                    Object[] paramValues = null;
                    if(mi.getParamCount()>0)
                    {
                        String nullParams = queryParams.get("nullParams");
                        Set<Integer> nullParamsIndex = buildSet(nullParams);
                        paramValues = new Object[mi.getParamCount()];
                        for(int i=0;i<mi.getParams().size();i++)
                        {
                            ParamInfo pi = mi.getParams().get(i);
                            //空值的设置。
                            if(nullParamsIndex != null && nullParamsIndex.contains(i+1)){
                                if(fundamentalType.contains(pi.paramType)){//基本类型不能设置空值
                                    json.put("success", "false");
                                    json.put("errorMsg", "The "+(i+1)+"th param is fundamental type, cannot set null");
                                    array.add(json);
                                    array.add(method);
                                    return array.toString();
                                }
                                paramValues[i] = null;
                                continue;
                            }

                            String paramValueStr = queryParams.get("param"+(i+1));
                            if(paramValueStr!=null)
                            {
                                Object paramValue = null;
                                try
                                {
                                    paramValue = pi.convertFromString(paramValueStr);
                                    paramValues[i]=paramValue;
                                }
                                catch(Exception e)
                                {
                                    json.put("success", "false");
                                    json.put("errorMsg", "The "+(i+1)+"th param is malformed: "+paramValueStr);
                                    array.add(json);
                                    array.add(method);
                                    return array.toString();
                                }
                            }
                            else
                            {
                                json.put("success", "false");
                                json.put("errorMsg", "The "+(i+1)+"th param is missing or null");
                                array.add(json);
                                array.add(method);
                                return array.toString();
                            }
                        }
                    }
                    try
                    {
                        AteyePrintStreamFactory.put();
                        Object bean = beans.get(beanName);
                        Object value = null;
                        if(bean instanceof Advised && mi.isTargetSpecial())
                        {
                            value = mi.getMethod().invoke(((Advised)bean).getTargetSource().getTarget(), paramValues);
                        }
                        else
                        {
                            value = mi.getMethod().invoke(beans.get(beanName), paramValues);
                        }
                        json.put("success", "true");
                        if(value!=null)
                        {
                            json.put("returnValue", convertOutputToHtmlForm(value.toString()));
                        }
                        else
                        {
                            json.put("returnValue", "void");
                        }
                        String ateyeOut = AteyePrintStreamFactory.getAteyeOut();
                        json.put("ateyeOut", ateyeOut);
                    }
                    catch(InvocationTargetException ie)
                    {
                        json.put("success", "true");
                        json.put("exception", ManagerLoggerUtil.convertThrowableToString(ie.getCause()!=null?ie.getCause():ie));
                    }
                    catch(Exception e)
                    {
                        json.put("success", "true");
                        json.put("exception", ManagerLoggerUtil.convertThrowableToString(e));
                    }
                }
                else
                {
                    json.put("success", "false");
                    json.put("errorMsg", "Invalid Signature:" + signature);
                }
            }
            else
            {
                json.put("success", "false");
                json.put("errorMsg", "Invalid BeanName:" + beanName);
            }
            array.add(json);
            array.add(method);
            return array.toString();
        }
        else
        {
            JSONObject json = new JSONObject();
            json.put("success", "false");
            json.put("errorMsg", "Invalid   type:" + action);
            return json.toString();
        }
    }

    /**
     * @param nullParams
     * @return
     */
    public  Set<Integer> buildSet(String nullParams) {
        Set<Integer> paramSet = new HashSet<Integer>();
        if(StringUtils.isBlank(nullParams)){
            return paramSet;
        }
        String[] splitValues = nullParams.split(",");
        for(String value : splitValues){
            if(StringUtils.isBlank(value)){
                continue;
            }
            try{
                Integer index = Integer.parseInt(value);
                paramSet.add(index);
            }catch (Exception e) {
                continue;
            }
        }
        return paramSet;
    }

    private JSONArray queryAllInvokersInfo()
    {
        JSONArray methods = new JSONArray();
        for (Map.Entry<String, Map<String, MethodInfo>> entry_out : invokers
                .entrySet())
        {
            String beanName = entry_out.getKey();
            Map<String, MethodInfo> value = entry_out.getValue();
            for (Map.Entry<String, MethodInfo> entry_in : value.entrySet()) {
                String signature = entry_in.getKey();
                JSONObject method = querySingleInvokerInfo(beanName,signature);
                methods.add(method);
            }
        }
        return methods;
    }

    //查询单个invoker
    private JSONObject querySingleInvokerInfo(String beanName,String signature)
    {
        JSONObject json = new JSONObject();
        if(invokers.get(beanName)!=null)
        {
            if(invokers.get(beanName).get(signature)!=null)
            {
                MethodInfo mi = invokers.get(beanName).get(signature);
                json.put("beanName", beanName);
                json.put("signature", signature);
                json.put("desc", mi.getDesc());
                json.put("paramCount", mi.getParamCount());
                json.put("paramDesc", mi.getParamDesc());
                json.put("invokerType", mi.getType());//增加type字段
                JSONArray params = new JSONArray();
                for(ParamInfo pi:mi.getParams())
                {
                    JSONObject param = new JSONObject();
                    param.put("type", pi.getParamType().getName());
                    params.add(param);
                }
                json.put("params", params);
            }
            else
            {
                json.put("success", "false");
                json.put("errorMsg", "Invalid Signature:" + signature);
            }
        }
        else
        {
            json.put("success", "false");
            json.put("errorMsg", "Invalid BeanName:" + beanName);
        }
        return json;
    }

    @Override
    public void init(ServletContext servletContext, PrintStream initLogger,Map<String,Object> beans) {
        this.info = initLogger;
        this.beans=beans;

        for(Map.Entry<String, Object> entry:beans.entrySet())
        {
            String beanName=entry.getKey();
            Object bean=entry.getValue();
            Map<String/*MethodSignature*/,MethodInfo> invokersOfABean = getAllInvokersOfABean(beanName,bean);
            invokers.put(beanName, invokersOfABean);
        }
        logger.info("远程方法调用数量: "+getMonitoredMethodCount());
    }

    private Map<String/*MethodSignature*/,MethodInfo> getAllInvokersOfABean(String beanName,Object bean)
    {
        Map<String/*MethodSignature*/,MethodInfo> invokersOfABean = new HashMap<String/*MethodSignature*/,MethodInfo>();
        Class<?> c = bean.getClass();
        Class<?> originClass = AopUtils.getTargetClass(bean);
        info.println();
        info.println("=====Scanning Bean named: "+beanName+" start");
        info.println("Class: "+c.getCanonicalName()+" OriginClass: "+originClass.getCanonicalName());
        for(Method m :originClass.getMethods())
        {
            Imt iv = m.getAnnotation(Imt.class);
            if(iv!=null)
            {
                MethodInfo mi = null;
                //存在代理类
                if(c!=originClass)
                {
                    //尝试获得代理类的对应方法，保持AOP的功能
                    try
                    {
                        Method proxyMethod = c.getMethod(m.getName(), m.getParameterTypes());
                        mi = new MethodInfo(proxyMethod);
                    }
                    catch(Exception e)
                    {
                        info.println("Exception generated when checking the counter method named: "+m.getName()+" from class named: "+c.getCanonicalName());
                        e.printStackTrace(info);
                        //存在代理类，但添加AteyeInvoker注释的方法没有被代理，直接采用Target（原始类）的Method
                        mi = new MethodInfo(m);
                        //要用Target对象invoke
                        mi.setTargetSpecial(true);
                    }
                }
                else
                {
                    //不存在代理类
                    mi = new MethodInfo(m);
                }
                if(iv.mehtodDescrption()!=null&&!"".equals(iv.mehtodDescrption()))
                {
                    mi.setDesc(iv.mehtodDescrption());
                }
                if(iv.paramDescrption()!=null)
                {
                    String[] params = iv.paramDescrption();
                    String paramDesc = null;
                    for (int i = 0; i < params.length; i++) {
                        if (i == 0) {
                            paramDesc = params[i];
                        } else {
                            paramDesc = paramDesc + "&" + params[i];
                        }
                    }
                    mi.setParamDesc(paramDesc);
                }
                mi.setType(iv.type()); //设置type值；
                invokersOfABean.put(mi.getSignature(), mi);
                info.println("Monitored Method: "+mi.getSignature());
            }
            else
            {
                info.println("Ordinary Method: "+m.getName());
            }
        }
        info.println("=====Scanning Bean named: "+beanName+" end");
        info.println();
        return invokersOfABean;
    }

    @Override
    public ManagerType getType() {
        return ManagerType.INVOKER;
    }

    private static class MethodInfo
    {
        private String paramDesc="";
        private Method method;
        private String signature;
        private int paramCount;
        private String desc;
        private boolean targetSpecial = false;

        private InvokerType type;
        private List<ParamInfo> params = new ArrayList<ParamInfo>();

        public MethodInfo(Method method)
        {
            this.method = method;
            this.paramCount = method.getParameterTypes().length;
            generateSignatureAndParamInfo();
        }

        private void generateSignatureAndParamInfo()
        {
            StringBuilder sign = new StringBuilder();
            sign.append(method.getName());
            sign.append("(");
            boolean endsWithComma = false;
            for(Class<?> paramType:method.getParameterTypes())
            {
                params.add(new ParamInfo(paramType));
                sign.append(paramType.getName());
                sign.append(",");
                endsWithComma = true;
            }
            //去掉最后一个","
            if(endsWithComma)
            {
                sign.deleteCharAt(sign.length()-1);
            }
            sign.append(")");
            signature=sign.toString();
            desc = signature;
        }



        public InvokerType getType() {
            return type;
        }

        public void setType(InvokerType type) {
            this.type = type;
        }

        public String getSignature() {
            return signature;
        }

        public int getParamCount() {
            return paramCount;
        }

        public List<ParamInfo> getParams() {
            return params;
        }

        public Method getMethod() {
            return method;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getParamDesc() {
            return paramDesc;
        }

        public void setParamDesc(String paramDesc) {
            this.paramDesc = paramDesc;
        }

        public boolean isTargetSpecial() {
            return targetSpecial;
        }

        public void setTargetSpecial(boolean targetSpecial) {
            this.targetSpecial = targetSpecial;
        }
    }

    private static class ParamInfo
    {
        private Class<?> paramType;

        public Class<?> getParamType() {
            return paramType;
        }

        public ParamInfo(Class<?> paramType)
        {
            this.paramType = paramType;
        }

        public Object convertFromString(String paramValueStr) throws Exception
        {
            Object paramValue = null;
            Class<?> bigType = typeConverter.get(paramType);
            if(bigType==String.class)
            {
                paramValue=paramValueStr;
            }
            else
            {
                if (bigType == Character.class) {
                    if(paramValueStr.length()>0)
                    {
                        paramValue=paramValueStr.toCharArray()[0];
                    }
                    else
                    {
                        paramValue=null;
                    }
                }else if(bigType == Date.class){
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date=format.parse(paramValueStr);
                    paramValue=date;
                } else {
                    Method transformedMethod = bigType.getMethod("valueOf",
                            String.class);
                    paramValue = transformedMethod.invoke(null,paramValueStr);
                }
            }
            return paramValue;
        }
    }

    //将Java的\n转换为网页中的<br/>
    private String convertOutputToHtmlForm(String originOuput)
    {
        if(originOuput!=null)
        {
            return originOuput.replace("\n", "<br/>");
        }
        else
        {
            return "void";
        }
    }

    //计算全部可远程控制的方法的数量
    private int getMonitoredMethodCount()
    {
        int count = 0;
        for(Map<String/*MethodSignature*/,MethodInfo> methodsOfABean:invokers.values())
        {
            count+=methodsOfABean.size();
        }
        return count;
    }
}

