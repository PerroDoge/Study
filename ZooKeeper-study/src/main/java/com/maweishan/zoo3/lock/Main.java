package com.maweishan.zoo3.lock;


import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper实现分布式锁
 * @author Doge
 */
public class Main {

    public static void main(String[] args) {
        ZooKeeper zooKeeper = ZooKeeperManager.getZooKeeper();
        // 开启10个线程，让他们去争抢锁
        for(int i = 0; i < 10; i ++) {
            new Thread(() -> {
                String threadName = Thread.currentThread().getName();
                WatcherCallBack watcherCallBack = new WatcherCallBack();
                watcherCallBack.setZooKeeper(zooKeeper);
                watcherCallBack.setThreadName(threadName);

                // 尝试获得锁
                watcherCallBack.tryLock();

                System.out.println(threadName+"::::"+watcherCallBack.getLockName()+"::: I'm working!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 用完释放锁
                watcherCallBack.unLock();
            }).start();
        }
        while(true) {

        }
    }
}
