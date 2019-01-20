package com.xxx.rpc.registry.zookeeper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClearCacheTask implements Runnable{

    private Map<String, Set<AddressEntity>> cacheAddress;
    private Long time = 15*60*1000L;

    public ClearCacheTask() {
    }

    public ClearCacheTask(
        Map<String, Set<AddressEntity>> cacheAddress) {
        this.cacheAddress = cacheAddress;
    }

    // 更新缓存内容，每个key对应的值保存时间超过15分钟就清除
    @Override
    public void run() {
        System.out.println("执行清除缓存！");
        Iterator<Entry<String, Set<AddressEntity>>> iterator = cacheAddress.entrySet().iterator();
        while (iterator.hasNext()) {
            Set<AddressEntity> set = iterator.next().getValue();
            set.removeIf(v -> System.currentTimeMillis() - v.getTimeStamp() > time);
            if (set.size() == 0) {
                iterator.remove();
            }
        }
    }
}
