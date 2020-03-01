package com.maweishan.zoo.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author Doge
 */
public class DefaultWatcher implements Watcher {
    private static CountDownLatch latch = null;
    public DefaultWatcher(CountDownLatch latch) {
        DefaultWatcher.latch = latch;
    }
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState state = watchedEvent.getState();
        switch(state) {

            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("the session is sync-connected!");
                latch.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }

    }
}
