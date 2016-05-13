package com.alibaba.imt.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.alibaba.imt.config.AteyeServletConfig;
import com.alibaba.imt.constants.ManagerType;
import com.alibaba.imt.excaption.AteyeInitException;
import com.alibaba.imt.log.Log4jFacility;
import com.alibaba.imt.manager.AteyeManager;
import com.alibaba.imt.manager.BaseManagerBeansUtil;
import com.alibaba.imt.util.ManagerLoggerUtil;

/**
 * 参数
    <init-param>
        <param-name>isRecordErrorLogger</param-name>
        <param-value>true</param-value>
    </init-param>
 * @author leconte
 *
 */
public class AteyeServlet extends HttpServlet {
    private static final long serialVersionUID = -4382366812508979253L;
    private static Logger logger = Logger.getLogger("ateyeClient");
    private Map<ManagerType, AteyeManager> managerMap = new HashMap<ManagerType, AteyeManager>();
    private String webx2Config=null;
    public static Date startUpTime=new Date();

    @SuppressWarnings("unchecked")
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("GBK");
        response.setCharacterEncoding("gb2312");
        response.setContentType("application/json;charset=gb2312");
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");
        ManagerType managerType = ManagerType.valueOf(type);
        if (managerType != null) {
            AteyeManager manager = managerMap.get(managerType);
            if (manager != null) {
                out.println(manager.service(convertParamMap(request.getParameterMap())));
            } else {
                logger.error("未找到针对请求类型:" + type + "的Manager");
            }
        } else {
            logger.error("无效的请求类型Type:" + type);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

    @Override
    public void init() 
    {
        long tic = System.currentTimeMillis();
        try{
            String uuid = UUID.randomUUID().toString();
            MDC.put("uuid", uuid);
            MDC.put("start", System.currentTimeMillis());
            //0.初始化client的Logger
            Log4jFacility.addAteyeClientLogger();
            logger.info("AteyeClient Logger初始化成功");
            //1.servlet参数获取
            getPara();
            
            //获得系统bootstrap的Logger
            PrintStream bootstrapLogger = ManagerLoggerUtil.getBootstrapLogger();
            
            //人为推迟init方法的执行，避免Trigger出现‘double’现象
            try
            {
                bootstrapLogger.println("AgentServlet初始化休眠（2s）");
                TimeUnit.SECONDS.sleep(2);
                bootstrapLogger.println("AgentServlet结束休眠");
            } 
            catch (InterruptedException e1) 
            {
                bootstrapLogger.println("AgentServlet初始化休眠被interrupt");
            }
            
            //全部bean
            Map<String, Object> beans =new HashMap<String, Object>();
            //webx2标识参数
            webx2Config=this.getInitParameter("webx2Config");
            //获取基本Spring的bean
            beans = BaseManagerBeansUtil.getAllBeans(this.getServletContext(), bootstrapLogger);
            logger.info("spring容器里bean数量:"+beans.size());
    
            //确定为webx2环境
            if(StringUtils.isNotBlank(webx2Config))
            {
                Map<String, Object> allBeansFromWebx2 = BaseManagerBeansUtil.getAllBeansFromWebx2(webx2Config, bootstrapLogger);
                logger.info("webx2环境,webx2容器里bean数量:"+beans.size());
                beans.putAll(allBeansFromWebx2);
            }
            else
            {
                bootstrapLogger.println("非Webx2环境");
            }
            //将ateye servlet的配置作为一个bean加入控制
            beans.put("___ateye_servlet_config___", new AteyeServletConfig());
            logger.info("添加配置bean:___ateye_servlet_config___");
            //将系统bootstrap的日志stream置为无效
            bootstrapLogger.flush();
            ManagerLoggerUtil.invalidBootstrapStream();
            
            //开始初始化具体的Manager
            for (ManagerType type : ManagerType.values())
            {
                AteyeManager manager = null;
                Class<? extends AteyeManager> managerClass = type.getManagerClass();
                try
                {
                    manager = managerClass.newInstance();
                } catch (Throwable e) {
                    logger.error("实例化" + managerClass.getName() + "失败", e);
                    continue;
                }
                try
                {
                    PrintStream managerLogger = ManagerLoggerUtil.getManagerLogger(type);
                    manager.init(this.getServletContext(), managerLogger, beans);
                    logger.info("初始化"+managerClass.getName()+"完成");
                    managerLogger.flush();
                    ManagerLoggerUtil.invalidInitLogger(type);
                } catch(AteyeInitException ai) {
                    logger.error("初始化" + managerClass.getName() + "严重异常，应用无法启动", ai);
                    throw ai;
                } catch(Throwable e) {
                    logger.error("初始化" + managerClass.getName() + "异常", e);
                    continue;
                }
                managerMap.put(type, manager);
            }
        }catch(Throwable t){
            logger.fatal("AteyeServlet初始化异常",t);
        }finally{
            logger.info("AteyeServlet初始化结束，耗时"+(System.currentTimeMillis()-tic)+"ms");
        }
        logger.info("AteyeServlet End");
        
    }
    


    private void getPara() {
        String isRecord = getInitParameter("isRecordErrorLogger");
        if ( isRecord != null && isRecord.equals("false")){
            AteyeServletConfig.isRecordErrorLogger = Boolean.FALSE;
            logger.warn("参数:isRecordErrorLogger的值为False，关闭错误日志收集");
        }
        String isRecordLog4j = getInitParameter("isRecordLog4j");
        if ( isRecordLog4j != null && isRecordLog4j.equals("false")){
            AteyeServletConfig.isRecordLog4j = Boolean.FALSE;
            logger.warn("参数:isRecordLog4j的值为False，关闭Log4j错误日志收集");
        }
        AteyeServletConfig.appName = getInitParameter("app");
        if ( StringUtils.isBlank(AteyeServletConfig.appName) ){
            logger.warn("参数app没有设置，将无法使用某些功能");
            AteyeServletConfig.appName = "";
        }else{
            logger.info("参数app:"+AteyeServletConfig.appName);
        }
        String isRecordSql = getInitParameter("recordSql");
        if ( isRecordSql != null && isRecordSql.equals("false")){
            AteyeServletConfig.isRecordSqlStat = Boolean.FALSE;
            logger.warn("参数:recordSql的值为False，关闭Sql埋点");
        }
        String kvTime = getInitParameter("kvTime");
        if ( kvTime != null ){
            Long kv = Long.valueOf(kvTime);
            AteyeServletConfig.setKvAwaitTime(kv);
            logger.warn("参数:kvAwaitTime修改为:"+kvTime);
        }
    }
    
    private Map<String,String> convertParamMap(Map<String,String[]> oriQueryMap) {
        Map<String,String> map = new HashMap<String,String>();
        for(Map.Entry<String,String[]> entry : oriQueryMap.entrySet()) {
            if(entry.getValue().length > 0) {
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
}
