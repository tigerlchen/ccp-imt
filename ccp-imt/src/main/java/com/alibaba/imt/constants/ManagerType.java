package com.alibaba.imt.constants;

import com.alibaba.imt.manager.AteyeManager;
import com.alibaba.imt.manager.impl.InvokerManager;
import com.alibaba.imt.manager.impl.LogControlManager;

public enum ManagerType 
{
    INVOKER(InvokerManager.class),
    LOGCONTROL(LogControlManager.class),
    ;
    private Class<? extends AteyeManager> managerClass=null;
    public Class<? extends AteyeManager> getManagerClass() {
        return managerClass;
    }
    ManagerType(Class<? extends AteyeManager> managerClass)
    {
        this.managerClass=managerClass;
    }
    
    public static void main(String[] args) {
        Object obj = ManagerType.valueOf("INVOKER");
        System.out.println(obj);
    }
}

