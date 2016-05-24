package com.alibaba.imt;

import static com.alibaba.imt.util.StringUtil.trimToNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.imt.adapter.BeanAdapter;
import com.alibaba.imt.adapter.privileges.ImtPrivilege;
import com.alibaba.imt.bean.CheckResult;
import com.alibaba.imt.bean.ImtGroup;
import com.alibaba.imt.bean.ImtInfo;
import com.alibaba.imt.bean.InterfaceInfo;
import com.alibaba.imt.handler.Handler;
import com.alibaba.imt.handler.imp.InterfaceHandler;
import com.alibaba.imt.scanner.Scanner;
import com.alibaba.imt.scanner.imp.SimpleClassScanner;
import com.alibaba.imt.util.Util;

/**
 * InterfaceManageToolBean
 *
 */
public class InterfaceManagementTool{
    private Set<String> paths;
    private BeanAdapter beanAdapter;
    private Scanner scanner;
    private Handler handler;
    private List<Map<String,Object>> dataList = null;
    private List<Map<String,Object>> xmlDataList = null;
    private List<InterfaceInfo> interfaceInfoList = null;
    private Map<String, InterfaceInfo> interfaceInfoMap = null;
    private boolean remoteManage = false;
    private boolean annotationScan = false;
    private ImtPrivilege imtPrivilege;
    private List<ImtGroup> imtGroups;

    public void init(){
        try{
            if(dataList == null){
                dataList = new ArrayList<Map<String,Object>>();
            }else{
                dataList.clear();
            }
            if(scanner == null){
                scanner = new SimpleClassScanner();
            }
            Set<String> classResourceSet = scanner.scan(paths);
            if(handler == null){
                handler = new InterfaceHandler();
            }
            for(String classResource : classResourceSet){
                handler.handleClass(classResource, dataList);
            }
            if(xmlDataList != null){
                dataList.addAll(xmlDataList);
            }
        }catch(Exception e){
            //There is for container can start up normally
            dataList = null;
            e.printStackTrace();
        }

    }

    private void initData(){
        if(interfaceInfoList == null || interfaceInfoMap == null){
            interfaceInfoList = new ArrayList<InterfaceInfo>();
            interfaceInfoMap = new HashMap<String, InterfaceInfo>();
        }else{
            interfaceInfoList.clear();
            interfaceInfoMap.clear();
        }
        for(Map<String,Object> data : dataList){
            if(beanAdapter != null){
                Object[] additionalDatas = beanAdapter.getBeanDatas((Class<?>)data.get("clazz"));
                data.put("additionalDatas", additionalDatas);
            }
            InterfaceInfo ii = Util.mapToBean(data, InterfaceInfo.class);
            ImtInfo imtInfo = Util.mapToBean(data, ImtInfo.class);

            if (ii.getDatas() != null && ii.getDatas().length  >= 4) {
                imtInfo = new ImtInfo();
                imtInfo.setMehtodDescrption(ii.getDatas()[0]);
                imtInfo.setGroup(new String[] {ii.getDatas()[2], ii.getDatas()[3]});
                imtInfo.setEnv(ii.getDatas()[1]);
                int paraLength = ii.getDatas().length -4;
                if (paraLength > 0) {
                    String[] params = new String[paraLength];
                    for (int i  = 0; i < paraLength; i++) {
                        params[i] = ii.getDatas()[4 + i];
                    }
                    imtInfo.setParamDescrption(params);
                }
            }

            if (null != ii && null != imtInfo) {
                ii.setImtInfo(imtInfo);
            }

            interfaceInfoList.add(ii);
            interfaceInfoMap.put(ii.getKey(), ii);
        }
    }

    public void initGroups() {
        if (null == imtGroups) {
            imtGroups = new ArrayList<ImtGroup>();
            List<InterfaceInfo> interfaceInfos = getInterfaceInfoList();
            for (InterfaceInfo info : interfaceInfos) {
                String[] datas = info.getDatas();
                if (null != info.getImtInfo() && null != trimToNull(info.getImtInfo().getMehtodDescrption())) {
                    ImtInfo imtInfo = info.getImtInfo();
                    String[] groupsArray = imtInfo.getGroup();
                    ImtGroup imtGroup = null;
                    if (null != groupsArray && groupsArray.length > 0) {
                        imtGroup = new ImtGroup(groupsArray[0]);

                        int index = imtGroups.indexOf(imtGroup);
                        if (index != -1) {
                            imtGroup =  imtGroups.get(index);
                        } else {
                            imtGroups.add(imtGroup);
                        }

                        ImtGroup previous = imtGroup;
                        for (int i = 1; i < groupsArray.length; i++) {
                            ImtGroup nextGroup = previous.getNextGroupByName(groupsArray[i]);
                            if (null == nextGroup) {
                                nextGroup = new ImtGroup(groupsArray[i]);
                                previous.addNext(nextGroup);
                            }
                            previous = nextGroup;
                        }

                        previous.addInterfaceInfo(info);
                    } else {
                        imtGroup = new ImtGroup(imtInfo.getMehtodDescrption());
                        int index = imtGroups.indexOf(imtGroup);
                        if (index != -1) {
                            imtGroup =  imtGroups.get(index);
                        } else {
                            imtGroups.add(imtGroup);
                        }
                        imtGroup.addInterfaceInfo(info);
                    }

                } else if (null != datas && datas.length >= 3) {
                    //老注解以数组形式
                    ImtGroup group = new ImtGroup(datas[2]);
                    int index = imtGroups.indexOf(group);
                    if (index != -1) {
                        group =  imtGroups.get(index);
                    } else {
                        imtGroups.add(group);
                    }

                    try {
                        //next group 目前先支持二维
                        ImtGroup nextGroup = group.getNextGroupByName(datas[3]);
                        if (null == nextGroup) {
                            nextGroup = new ImtGroup(datas[3]);
                            group.addNext(nextGroup);
                        }

                        nextGroup.addInterfaceInfo(info);
                    } catch (IndexOutOfBoundsException e) {
                        // 一维分组
                        group.addInterfaceInfo(info);
                    }
                }
            }
        }
    }

    public List<InterfaceInfo> getInterfaceInfoList(boolean refresh){
        if(refresh){
            init();
        }
        if(interfaceInfoList == null || refresh){
            initData();
        }
        return this.interfaceInfoList;
    }

    public List<InterfaceInfo> getInterfaceInfoList(){
        return getInterfaceInfoList(false);
    }

    public Map<String, InterfaceInfo> getInterfaceInfoMap(boolean refresh){
        if(refresh){
            init();
        }
        if(interfaceInfoMap == null || refresh){
            initData();
        }
        return this.interfaceInfoMap;
    }

    public Map<String, InterfaceInfo> getInterfaceInfoMap(){
        return getInterfaceInfoMap(false);
    }

    public Object invoke(String key, Object[] additionalDatas, Object[] args){
        InterfaceInfo ii = getInterfaceInfoMap().get(key);
        if(ii == null){
            throw new RuntimeException("No class and method can be found: " + key);
        }
        Class<?>[] argumentClasses = ii.getArgumentClasses();
        //防止空指针
        if (null == args) {
            args = new Object[0];
        }
        args = Arrays.copyOfRange(args, 0, args.length, Object[].class);//make sure the args is an Object[]
        CheckResult<String> checkResult = checkAndConvertArgs(argumentClasses, args);
        if(!checkResult.isPassed()){
            return checkResult.getResult();
        }

        Class<?> clazz = ii.getClazz();

        Method method = null;
        Object result = null;
        Object object = null;
        try {
            method = clazz.getMethod(ii.getMethodName(), argumentClasses);
            if(Modifier.isStatic(method.getModifiers())){
                result = method.invoke(clazz, args);
            }else{
                if(beanAdapter != null){
                    object = beanAdapter.getObject(clazz, additionalDatas);
                    if(object == null){
                        throw new RuntimeException("No specified instance can be found via " + beanAdapter.getClass().getName() + ": class=" + clazz + ",additionalDatas=" + additionalDatas);
                    }
                    method = object.getClass().getMethod(ii.getMethodName(), argumentClasses);
                }else{
                    object = clazz.newInstance();
                }
                result = method.invoke(object, args);
            }

        } catch (Exception e) {
            String stackTrace = "";
            StringWriter writer = new StringWriter();
            try{
                e.printStackTrace(new PrintWriter(writer));
                stackTrace = writer.getBuffer().toString();
            }finally {
                if(writer != null)
                    try {
                        writer.close();
                    }catch (Exception ee) {
                        ee.printStackTrace();
                    }
            }
            //stackTrace = stackTrace.replaceAll("\r\n", "<br/>");
            StringBuilder sb = new StringBuilder("Method invoke has cause a exception :");
            sb.append(e.getMessage());
            sb.append("\r\n");
            sb.append(stackTrace);
            return sb.toString();
        }
        return result;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private CheckResult<String> checkAndConvertArgs(Class<?>[] argumentClasses, Object[] args) {
        CheckResult<String> cr = new CheckResult<String>();
        cr.setPassed(true);

        int argumentClassesLength = argumentClasses.length;
        int argsLength = args.length;
        if(argumentClassesLength != argsLength){
            cr.setPassed(false);
            cr.setResult("Argument count is wrong, expect " + argumentClassesLength + ", but " + argsLength);
            return cr;
        }
        for(int i = 0; i < argumentClassesLength; i++){
            Class<?> argumentClass = argumentClasses[i];
            Object arg = args[i];
            if(argumentClass.isInstance(arg)){
                continue;
            }
            try{
                String argStr = (String)arg;
                if(argumentClass.isPrimitive()){
                    if (argumentClass == Integer.TYPE) {
                        args[i] = Integer.valueOf(argStr);
                    }  else if (argumentClass == Boolean.TYPE) {
                        args[i] = Boolean.valueOf(argStr);
                    } else if (argumentClass == Byte.TYPE) {
                        args[i] = Byte.valueOf(argStr);
                    } else if (argumentClass == Character.TYPE) {
                        if(argStr.length() != 1){
                            throw new IllegalArgumentException(argStr + " is not a char.");
                        }
                        args[i] = Character.valueOf(argStr.charAt(0));
                    } else if (argumentClass == Short.TYPE) {
                        args[i] = Short.valueOf(argStr);
                    } else if (argumentClass == Double.TYPE) {
                        args[i] = Double.valueOf(argStr);
                    } else if (argumentClass == Float.TYPE) {
                        args[i] = Float.valueOf(argStr);
                    } else /* if (c == Long.TYPE) */{
                        args[i] = Long.valueOf(argStr);
                    }
                }else if(argumentClass.isArray()){
                    args[i] = convertArrayArgument(argStr, argumentClass.getComponentType());
                }else if(argumentClass.isEnum()){
                    args[i] = Enum.valueOf((Class<? extends Enum>)argumentClass, argStr);
                }else{
                    args[i] = JSON.parseObject(argStr, argumentClass);
                }
            }catch(RuntimeException re){
                cr.setPassed(false);
                cr.setResult("The type of argument at " + (i + 1) + " is wrong, expect " + argumentClass.getName() + ", but cannot convert the value of " + arg + " from the type " + arg.getClass().getName());
                return cr;
            }
        }
        return cr;
    }

    /**
     * @param argStr
     * @param componentType
     * @return
     */
    private <T>T[] convertArrayArgument(String argStr, Class<T> componentType){
        List<T> list = JSON.parseArray(argStr, componentType);
        @SuppressWarnings("unchecked")
        T[] arr = list.toArray((T[])Array.newInstance(componentType, 0));
        return arr;
    }

    public Set<String> getPaths() {
        return paths;
    }

    public void setPaths(Set<String> paths) {
        this.paths = paths;
    }

    public BeanAdapter getBeanAdapter() {
        return beanAdapter;
    }

    public void setBeanAdapter(BeanAdapter beanAdapter) {
        this.beanAdapter = beanAdapter;
    }

    public List<Map<String, Object>> getXmlDataList() {
        return xmlDataList;
    }

    public void setXmlDataList(List<Map<String, Object>> xmlDataList) {
        this.xmlDataList = xmlDataList;
    }

    public boolean isRemoteManage() {
        return remoteManage;
    }

    public void setRemoteManage(boolean remoteManage) {
        this.remoteManage = remoteManage;
    }

    public boolean isAnnotationScan() {
        return annotationScan;
    }

    public void setAnnotationScan(boolean annotationScan) {
        this.annotationScan = annotationScan;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ImtPrivilege getImtPrivilege() {
        return imtPrivilege;
    }

    public void setImtPrivilege(ImtPrivilege imtPrivilege) {
        this.imtPrivilege = imtPrivilege;
    }


    public List<ImtGroup> getImtGroups() {
        return imtGroups;
    }

    public void setImtGroups(List<ImtGroup> imtGroups) {
        this.imtGroups = imtGroups;
    }

    public static void main( String[] args ){

        Set<String> paths = new HashSet<String>();
        paths.add("com.alibaba.imt");
        InterfaceManagementTool imt = new InterfaceManagementTool();
        imt.setPaths(paths);
        //imtb.setBeanAdapter(new RomaBeanAdapter());
        imt.init();
        List<InterfaceInfo> interfaceInfoList = imt.getInterfaceInfoList();
        for(InterfaceInfo interfaceInfo : interfaceInfoList){

            System.out.println("key:" + interfaceInfo.getKey());
            System.out.println("methodName:" + interfaceInfo.getMethodName());
            System.out.println("returnClass:" + interfaceInfo.getReturnClass());
            System.out.println("argumentClasses:" + interfaceInfo.getArgumentClasses());
            System.out.println("argumentClasses.num:" + interfaceInfo.getArgumentClasses().length);
            for(String group : interfaceInfo.getDatas()){
                System.out.println("group==" + group);
            }
        }
        Object obj = imt.invoke(Util.generateKey("com.alibaba.imt.Test", "testMethod2", "(ILjava/lang/String;)Ljava/lang/String;"), null, new Object[]{18, "你好"});
        System.out.println("this is result:" + obj);

    }
}
