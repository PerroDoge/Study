package com.maweishan.zoo2.conf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    CountDownLatch latch = new CountDownLatch(1);
    ZooKeeper zooKeeper;
    MyConf conf;

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    public void await() {
        zooKeeper.exists("/AppConf", this, this, "exists");

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * exists-StatCallback
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

            zooKeeper.getData("/AppConf", this, this, "getData");

    }

    /**
     * getData-DataCallBack
     * @param i
     * @param s
     * @param o
     * @param bytes
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if(bytes != null) {
            String data = new String(bytes);
            conf.setConf(data);
            latch.countDown();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                zooKeeper.getData("/AppConf", this, this, "getData after created");
                break;
            case NodeDeleted:
                latch = new CountDownLatch(1);
                conf.setConf("");
                break;
            case NodeDataChanged:
                zooKeeper.getData("/AppConf", this, this, "getData again!");
                break;
            case NodeChildrenChanged:
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
