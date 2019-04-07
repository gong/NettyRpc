package com.xxx.rpc.common.util.retry;
import java.util.concurrent.Callable;
import org.junit.Test;

public class RetryUtilTest {

    public boolean task(boolean flag){
        if (flag){
            System.out.println("执行任务成功！");
            return true;
        }else {
            System.out.println("执行任务失败！");
            throw new RuntimeException();
        }
    }

    @Test
    public void testCallFalse() {
        Callable<Boolean> callable = ()-> task(false);
        RetryUtil.call(callable, 3);
    }

    @Test
    public void testCallTrue() {
        Callable<Boolean> callable = ()-> task(true);
        RetryUtil.call(callable, 3);
    }
}
