package com.maweishan.zoo.conf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author Doge
 */
public class WatcherCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private  ZooKeeper zooKeeper = null;
    private MyConf myConf = null;
    private CountDownLatch latch = new CountDownLatch(1);
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {

        this.zooKeeper = zooKeeper;

    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }


    public void aWait() throws InterruptedException {
        zooKeeper.exists("/AppConf", this, this, "exists");

        latch.await();
    }

    /**
     * StatCallBack
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
                zooKeeper.getData(s,this, this, "getData");

    }

    /**
     * DataCallBack
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
           myConf.setConf(data);
           latch.countDown();
       }
    }

    /**
     * Watcher
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();

        switch (type) {
            case None:

                break;
            case NodeCreated:
                zooKeeper.getData("/AppConf",this,this,"sdfs");
                break;
            case NodeDeleted:
                myConf.setConf("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zooKeeper.getData("/AppConf", this, this, "Data was changed,get data again!");
                break;
            case NodeChildrenChanged:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }


}
