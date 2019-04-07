package com.xxx.rpc.client;

import com.xxx.rpc.common.bean.RpcRequest;
import com.xxx.rpc.common.bean.RpcResponse;
import com.xxx.rpc.common.util.StringUtil;
import com.xxx.rpc.common.util.retry.RetryUtil;
import com.xxx.rpc.registry.ServiceDiscovery;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * RPC 代理（用于创建 RPC 服务代理）
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建 RPC 请求对象并设置请求属性
                        RpcRequest request = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(), serviceVersion,
                            method.getName(), method.getParameterTypes(), args);
                        // 获取 RPC 服务地址
                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            if (StringUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        if (StringUtil.isEmpty(serviceAddress)) {
                            throw new RuntimeException("server address is empty");
                        }
                        // 从 RPC 服务地址中解析主机名与端口号
                        String[] array = StringUtil.split(serviceAddress, ":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        long time = System.currentTimeMillis();
                        Callable<RpcResponse> callable = () -> {
                            // 创建 RPC 客户端对象并发送 RPC 请求 需要把创建RPC客户端的代码放在匿名内部类里面
                            // 否则会导致ClosedChannelException异常，由于netty会在第一次连接后关闭客户端的channel
                            // 重试连接时需要重新创建RPC 客户端
                            RpcClient client = new RpcClient(host, port);
                            LOGGER.info("host:{},port:{}",host,port);
                            return client.send(request);};
                        RpcResponse response = RetryUtil.call(callable, 3);
                        LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
                        if (response == null) {
                            throw new RuntimeException("response is null");
                        }
                        // 返回 RPC 响应结果
                        if (response.hasException()) {
                            throw response.getException();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}
