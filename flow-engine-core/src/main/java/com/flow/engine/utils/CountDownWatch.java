package com.flow.engine.utils;
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

//    CountDownWatch watch = CountDownWatch.createAndStart(timeout, TimeUnit.MILLISECONDS);
//        for (
//    Future<PredictResult> result : results) {
//        // 倒计时方式，控制总的循环结束时间
//        PredictResult pr = null;
//        try {
//            pr = result.get(watch.millisLeft(), TimeUnit.MILLISECONDS);
//            List<WeightItem> items = pr.getWeightItems();
//            System.arraycopy(items.toArray(), 0, dest, destPos, items.size());
//            destPos += items.size();
//        } catch (Exception e) {
//            if (e instanceof TimeoutException) {
//                log.error("timeout for parallel predicting tasks, time cost {}ms", watch.elapsedMillis());
//            } else if (e instanceof ArrayIndexOutOfBoundsException) {
//                log.error("expected item size {}, actual got {}, destPos {}, dest size {}",
//                        null == pr ? 0 : pr.getExpectSize(), null == pr ? 0 : pr.getWeightItems().size(),
//                        destPos, dest.length);
//            } else {
//                log.error("failed to get all future task for predicting, time cost {}ms", watch.elapsedMillis(), e);
//            }
//            unfinished = true;
//            break;
//        }
//    }
}
