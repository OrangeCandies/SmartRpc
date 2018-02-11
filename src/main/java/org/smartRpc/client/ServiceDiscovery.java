package org.smartRpc.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.util.ConfigUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceDiscovery {
    private static final Logger LOGGER  = LoggerFactory.getLogger(ServiceDiscovery.class);
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String registryAddress;
    private ZooKeeper zooKeeper;
    private volatile List<String> dataList;
    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
        zooKeeper = connectServer();
        if(zooKeeper != null){
            checkNode(zooKeeper);
        }
    }

    public String discovery(){
        if(dataList != null && dataList.size()>0){
            if(dataList.size() == 1){
                return dataList.get(0);
            }else{
                return dataList.get(ThreadLocalRandom.current().nextInt(dataList.size()));
            }
        }
        new Throwable(" there no server can be used").printStackTrace();
        return null;
    }
    //检查子节点 更新可用节点列表
    private void checkNode(ZooKeeper zooKeeper) {
        try {
            List<String> children = zooKeeper.getChildren(ConfigUtil.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                        checkNode(zooKeeper);
                    }
                }
            });
            List<String> lists = new ArrayList<>();
            for(String node : children){
                byte[] data = zooKeeper.getData(ConfigUtil.ZK_REGISTRY_PATH + "/" + node, false, null);
                lists.add(new String(data));
            }
            this.dataList = lists;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(registryAddress, ConfigUtil.ZK_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        countDownLatch.countDown();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return zooKeeper;
    }



}
