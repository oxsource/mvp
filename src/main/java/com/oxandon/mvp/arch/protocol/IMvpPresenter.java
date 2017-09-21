package com.oxandon.mvp.arch.protocol;

/**
 * Created by peng on 2017/5/20.
 */

public interface IMvpPresenter extends IMvp {
    String REPEAT_CHECK = "repeat_check";
    String REPEAT_QUEUE = "repeat_queue";//任务队列

    /**
     * 拦截请求消息
     *
     * @param msg
     * @return
     * @throws Exception
     */
    boolean onIntercept(IMvpMessage msg) throws Exception;

    /**
     * 分配请求消息
     *
     * @param msg
     * @return
     * @throws Exception
     */
    boolean distribute(IMvpMessage msg) throws Exception;

    /**
     * 从消息任务队列中移除指定请求消息
     *
     * @param message
     */
    void removeTask(IMvpMessage message);

    /**
     * IMvpDispatcher持有对象
     *
     * @return
     */
    IMvpDispatcher dispatcher();

    /**
     * 销毁
     */
    void destroy();
}