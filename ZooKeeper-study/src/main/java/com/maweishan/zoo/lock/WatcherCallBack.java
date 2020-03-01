package com.maweishan.zoo.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatcherCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    ZooKeeper zooKeeper = null;
    CountDownLatch latch = new CountDownLatch(1);
    String lockName;
    String threadName = Thread.currentThread().getName();
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void trylock() throws Exception{

        zooKeeper.create("/lock", Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "create");

        latch.await();
    }

    public void unlock() throws KeeperException, InterruptedException {
        zooKeeper.delete("/"+lockName, -1);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();

        switch(type) {

            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zooKeeper.getChildren("/", false, this,"getChildren again");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }


    /**
     * create-StringCallBack
     * @param i
     * @param s
     * @param o
     * @param s1
     */
    @Override
    public void processResult(int i, String s, Object o, String s1) {

        zooKeeper.getChildren("/", false, this, "getChildren");
        lockName = s1.substring(1);


    }


    /**
     * getChildren-Children2CallBack
     * @param rc
     * @param s
     * @param o
     * @param list
     * @param stat
     */
    @Override
    public void processResult(int rc, String s, Object o, List<String> list, Stat stat) {
        Collections.sort(list);
        int i = list.indexOf(lockName);

        if(i == 0) {
            System.out.println(threadName+"I'm first...");
            latch.countDown();
        }else{

            zooKeeper.exists("/"+list.get(i-1), this, this, "watchBefore");
        }

    }

    /**
     * exists-StatCallBack
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }
}
