package com.maweishan.zoo2.lock;

import org.apache.zookeeper.ZooKeeper;

public class Main {

    public static void main(String[] args) {
        ZooKeeper zooKeeper = Zcon.getZooKeeper();

        for(int i = 0; i < 10; i++) {
            new Thread(() -> {
                WatcherCallBack watcherCallBack = new WatcherCallBack();
                String threadName = Thread.currentThread().getName();
                watcherCallBack.setThreadName(threadName);
                watcherCallBack.setZooKeeper(zooKeeper);

                watcherCallBack.tryLock();

                System.out.println(threadName+"I'm Workinng!");

                try {
                    Thread.sleep(1000);
                    watcherCallBack.unLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        while(true) {

        }
    }
}
