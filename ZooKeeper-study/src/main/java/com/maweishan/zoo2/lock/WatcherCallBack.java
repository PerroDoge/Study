package com.maweishan.zoo2.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatcherCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    ZooKeeper zooKeeper;
    String threadName;
    String nodeName;
    CountDownLatch latch = new CountDownLatch(1);
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void tryLock() {
        zooKeeper.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "create");

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void unLock() {
        try {
            zooKeeper.delete("/"+nodeName, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void processResult(int i, String s, Object o, String s1) {
        nodeName = s1.substring(1);
        zooKeeper.getChildren("/", false, this, "getChildren");
    }

    @Override
    public void processResult(int rc, String s, Object o, List<String> list, Stat stat) {
        if(list == null) {
            System.out.println("List is NULL!!!!");
        }else {
            Collections.sort(list);
            int i = list.indexOf(nodeName);
            if(i == 0) {
                System.out.println(threadName+":::"+nodeName+"::: I'm first");
                latch.countDown();
            }else{
                zooKeeper.exists("/"+list.get(i-1), this, this, "exists");
            }
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();

        switch (type) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                System.out.println("delete");
                zooKeeper.getChildren("/", false, this, "getChildren again!");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
