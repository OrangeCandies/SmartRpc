package org.smartRpc.manager;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {

    private static final ConcurrentHashMap<String,Object> SERVICE_CONTAINER = new ConcurrentHashMap<String, Object>();

    public static void setServiceClass(String serverName,Object o){
        SERVICE_CONTAINER.put(serverName,o);
    }

    public static Object getServiceObject(String serverName){
        return SERVICE_CONTAINER.get(serverName);
    }
}
