import com.xxx.rpc.registry.zookeeper.ZooKeeperServiceDiscovery;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZooKeeperServiceDiscoveryTest {

    @Test
    public void testGetDataByZkClientAndCacheData(){
        String servicePath = "com.xxx.rpc.sample.api.HelloService";
        ZooKeeperServiceDiscovery discovery = new ZooKeeperServiceDiscovery("118.24.35.180:2181");
        String data = discovery.discover(servicePath);
        System.out.println(data);
        assertNotNull(ZooKeeperServiceDiscovery.cacheAddress.get("/registry/"+servicePath));
    }
}
