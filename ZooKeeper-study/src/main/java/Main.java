import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final String IP = "192.168.50.140,192.168.50.141,192.168.50.150/zkStudy";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    public static void main(String[] args) throws Exception {

          zooKeeper = new ZooKeeper(IP, 10000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("I'm defaultWatcher");
                Event.KeeperState state = watchedEvent.getState();
                switch(state){
                    case Unknown:
                        break;
                    case Disconnected:
                        System.out.println("DEAD");
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
                        break;
                }
            }
        });

        latch.await();
        String c1 = "First time created!";

                zooKeeper.exists("/c1", new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        System.out.println("I'm exists's watcher");
                        Event.EventType type = watchedEvent.getType();
                        switch (type) {

                            case None:
                                System.out.println("not found that node!");
                                break;
                            case NodeCreated:
                                System.out.println("the node is created!");
                                break;
                            case NodeDeleted:
                                System.out.println("the node is deleted!");
                                break;
                            case NodeDataChanged:
                                System.out.println("the node has changed!");
                                break;
                            case NodeChildrenChanged:
                                System.out.println("the node's children has changed!");
                                break;
                            default:
                                System.out.println("what?");
                        }
                    }
                }, new AsyncCallback.StatCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, Stat stat) {
                        System.out.println(" exist??? i = " + i + ", s = " + s + ",o = " + o + ",stat = ");


                    }
                }, "abc");



        Thread.sleep(2000000);
    }
}
