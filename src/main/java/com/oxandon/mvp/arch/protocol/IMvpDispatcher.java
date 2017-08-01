package com.oxandon.mvp.arch.protocol;

/**
 * MVP任务调度层
 * Created by peng on 2017/5/20.
 */

public interface IMvpDispatcher extends IMvp{
    /**
     * 与View层绑定
     *
     * @param view
     */
    void attach(IMvpView view);

    /**
     * 与View层解绑
     *
     * @param view
     */
    void detach(IMvpView view);

    /**
     * 派发消息至Presenter
     *
     * @param msg
     * @return
     */
    boolean dispatchToPresenter(IMvpMessage msg);

    /**
     * 派发消息至View
     *
     * @param msg
     * @return
     */
    boolean dispatchToView(IMvpMessage msg);

    /**
     * 从View层获取数据
     *
     * @param msg
     * @return
     */
    Object provideFromView(IMvpMessage msg);
}