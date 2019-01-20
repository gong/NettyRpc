package com.xxx.rpc.registry.zookeeper;

import com.xxx.rpc.common.util.CollectionUtil;
import com.xxx.rpc.registry.ServiceDiscovery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;

    public static Map<String, Set<AddressEntity>> cacheAddress = new ConcurrentHashMap<>();

    static {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new ClearCacheTask(cacheAddress),10, 30, TimeUnit.SECONDS);
    }

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String name) {
        // 获取 service 节点
        String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
        String data;
        if ((data=getCacheData(servicePath))!=null){
            return data;
        }else {
            return getDataByZkClientAndCacheData(servicePath);
        }
    }

    private String getCacheData(String servicePath){
        if (cacheAddress.get(servicePath)==null) {
            return null;
        } else {
            return getOneAddressData(cacheAddress.get(servicePath));
        }
    }

    private String getDataByZkClientAndCacheData(String servicePath) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try {

            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            String address = getOneAddress(addressList);
            String data = zkClient.readData(servicePath + "/" + address);
            Set<AddressEntity> set = new HashSet<>();
            AddressEntity addressEntity = new AddressEntity(data, System.currentTimeMillis());
            set.add(addressEntity);
            cacheAddress.merge(servicePath, set, (oldV,newV)->{oldV.addAll(newV);return oldV;});
            return data;
        } finally {
            zkClient.close();
        }
    }

    private <T> T getOneValue(List<T> addressList) {
        // 获取 address 节点
        T address;
        int size = addressList.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = addressList.get(0);
            LOGGER.debug("get only address node: {}", address);
        } else {
            // 若存在多个地址，则随机获取一个地址
            address = addressList.get(ThreadLocalRandom.current().nextInt(size));
            LOGGER.debug("get random address node: {}", address);
        }
        return address;
    }

    private String getOneAddress(List<String> addressList) {
        return getOneValue(addressList);
    }

    private String getOneAddressData(Set<AddressEntity> addressListData) {
       AddressEntity addressEntity = getOneValue(new ArrayList<>(addressListData));
       return addressEntity.getAddress();
    }
}