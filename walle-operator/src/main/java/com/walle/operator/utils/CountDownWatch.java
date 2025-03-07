package com.walle.operator.utils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 倒计时
 * @author harley.shi
 * @company fordeal
 * @date 2020/6/4
 */
public class CountDownWatch {

    /**
     * 创建倒计时表，并启动
     * @param duration 倒计时时间
     * @return
     */
    public static CountDownWatch createAndStart(long duration, TimeUnit timeUnit) {
        return new CountDownWatch(timeUnit.toMillis(duration)).start();
    }

    private final static long NANO_2_MILLIS = 100_0000L;

    private long elapseTime;
    private long stopTime;

    public CountDownWatch(long timeToElapseInMillis) {
        this.elapseTime = timeToElapseInMillis * NANO_2_MILLIS;
    }

    /**
     * 启动倒计时
     */
    public CountDownWatch start() {
        this.stopTime = System.nanoTime() + elapseTime;
        return this;
    }

    /**
     * 到达倒计时结束的剩余时间
     * @return
     */
    public long millisLeft() throws TimeoutException {
        long left = stopTime - System.nanoTime();
        if (left < 1) {
            throw new TimeoutException("countdown " + elapseTime / NANO_2_MILLIS + "ms ended");
        }
        return left / NANO_2_MILLIS;
    }

    /**
     * 计时，毫秒数
     * @return
     */
    public long elapsedMillis() {
        return (System.nanoTime() + elapseTime - stopTime) / NANO_2_MILLIS;
    }

}
