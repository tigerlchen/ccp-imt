package com.alibaba.imt.manager;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.alibaba.imt.constants.ManagerType;
import com.alibaba.imt.excaption.AteyeInitException;
import com.alibaba.imt.util.ManagerLoggerUtil;

public class AteyeManagerContext {

    private static Logger logger = Logger.getLogger("ateyeClient");

    public static AteyeManagerContext INSTANCE = new AteyeManagerContext();

    private final Map<ManagerType, AteyeManager> managerMap = new HashMap<ManagerType, AteyeManager>();

    private AteyeManagerContext() {

    }

    public void init(Map<String, Object> beans, ServletContext servletContext) {
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
                manager.init(servletContext, managerLogger, beans);
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
    }

    public Map<ManagerType, AteyeManager> getManagerMap() {
        return managerMap;
    }

}
