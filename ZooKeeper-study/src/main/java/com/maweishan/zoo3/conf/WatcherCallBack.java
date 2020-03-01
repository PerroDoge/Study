package com.maweishan.zoo3.conf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author Doge
 * ZooKeper事件监视和操作类
 */
public class WatcherCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zooKeeper;
    private Config conf;
    private CountDownLatch latch = new CountDownLatch(1);

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setConf(Config conf) {
        this.conf = conf;
    }

    /**
     * 配置文件有两种可能，存在或者不存在
     */
    public void await() {
        // 首先检查配置文件存不存在
        zooKeeper.exists("/AppConf", this, this, "first exists");

        try {
            // 如果不存在就会一直阻塞
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * exists-StatCallBack
     * @param i  结果码-> i == 0, 该节点存在。i < 0, 该节点不存在
     * @param s  节点名称
     * @param o  调用exists方法时传进去的 ctx
     * @param stat  节点信息
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

        // 获取配置文件，如果中间被删除掉，watcher就会进行处理。
        zooKeeper.getData("/AppConf", this, this, "first getData");


    }

    /**
     * getData-DataCallBack
     * @param i
     * @param s
     * @param o
     * @param bytes 配置文件的字节数组
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        // 如果存在配置文件那么就使await()方法放行。
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
                // 当配置文件被创建时去获取
                zooKeeper.getData("/AppConf", this, this, "getData again");
                break;
            case NodeDeleted:
                // 当配置文件被删除时重新设置latch使其阻塞
                conf.setConf("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                // 如果配置文件的内容被改变那么需要重新获取
                zooKeeper.getData("/AppConf", this, this, "getData for data-changed");
                break;
            case NodeChildrenChanged:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
