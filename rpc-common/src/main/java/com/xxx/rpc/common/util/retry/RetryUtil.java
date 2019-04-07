package com.xxx.rpc.common.util.retry;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重试机制
 */
public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);
    public static <T> T call(Callable<T> callable,int num) {
        try {
            return callable.call();
        } catch (Exception e) {
            logger.error("任务需要重试");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                logger.error("线程休眠，发生中断异常！");
            }
        }
        int i=0;
        while (i<num){
            i++;
            try {
                return callable.call();
            } catch (Exception e) {
                logger.error("重试第{}次",i);
                logger.info(e.toString());
            }
            if (i==num){
                logger.error("任务重试执行失败！");
            }
        }
        return null;
    }
}
