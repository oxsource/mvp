package com.oxandon.mvp.arch.protocol;

/**
 * MVP用户视图层
 * Created by peng on 2017/5/20.
 */

public interface IMvpView extends IMvp {
    String STR_LOADING = "STR_LOADING";
    String BOOL_LOADING = "BOOL_LOADING";

    /**
     * View层对外提供数据
     *
     * @param msg 请求View层数据的消息
     * @return
     */
    Object provide(IMvpMessage msg);

    /**
     * View层向调度器发起请求
     *
     * @param msg 发往请求目标的消息
     * @return
     */
    boolean function(IMvpMessage msg);

    /**
     * View层接受调度器消息
     *
     * @param msg 来自调度层的消息
     * @return
     */
    boolean dispatch(IMvpMessage msg);
}