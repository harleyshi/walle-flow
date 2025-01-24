package com.flow.engine.threadpool;

import java.util.concurrent.RejectedExecutionHandler;

public interface XRejectedExecutionHandler extends RejectedExecutionHandler {

    long rejected();

}
