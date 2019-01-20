package com.xxx.rpc.sample.client;

import com.xxx.rpc.client.RpcProxy;
import com.xxx.rpc.sample.api.HelloService;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloClient {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);

        Thread.sleep(40000);
        HelloService helloService2 = rpcProxy.create(HelloService.class);
        String result2 = helloService2.hello("World2");
        System.out.println(result2);

        HelloService helloService3 = rpcProxy.create(HelloService.class, "sample.hello2");
        String result3 = helloService3.hello("世界");
        System.out.println(result3);

        System.exit(0);
    }
}
