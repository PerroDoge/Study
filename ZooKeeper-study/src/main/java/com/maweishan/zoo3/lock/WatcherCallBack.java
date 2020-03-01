package com.maweishan.zoo3.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatcherCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    ZooKeeper zooKeeper;
    String threadName;
    String lockName;
    CountDownLatch latch = new CountDownLatch(1);

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getLockName() {
        return lockName;
    }

    public void tryLock() {
        // 每个线程会创建一个锁
        zooKeeper.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "create lock");

        try {
            // 只有当自己的锁在锁集合里是第一个的时候才会放行。
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void unLock() {
        try {
            zooKeeper.delete("/"+lockName, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**create-StringCallBack
     * @param i
     * @param s
     * @param o
     * @param s1
     */
    @Override
    public void processResult(int i, String s, Object o, String s1) {
        // 取得包括自己在内的所有锁
        lockName = s1.substring(1);
        zooKeeper.getChildren("/", false, this, "getChildren");
    }

    /**getChildren-Children2CallBack
     * @param rc
     * @param s
     * @param o
     * @param list
     * @param stat
     */
    @Override
    public void processResult(int rc, String s, Object o, List<String> list, Stat stat) {
        if(list != null) {
            // 对每个线程创建的锁进行排序
            Collections.sort(list);
            int i = list.indexOf(lockName);

            if(i == 0) {
                // 只有当线程创建的锁在锁集合里是第一个的时候才能去拿着锁去执行自己的操作
                System.out.println(threadName+"I'm first");
                latch.countDown();
            }else{
                // 其他的锁监视自己前面的那个锁，如果前面那个锁不是第一个但是由于其他原因被删除了，那么就去监视那个锁的前一个锁。
                zooKeeper.exists("/"+list.get(i-1), this, this, "hey,bro! are you alive?");
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
                // 如果有锁被删除，那么重新获取锁集合排序。
                zooKeeper.getChildren("/", false, this, "I'm so bad,because my brother is dead");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
