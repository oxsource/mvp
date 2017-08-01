package com.oxandon.mvp.ui.fragment;

import com.oxandon.mvp.ui.activity.IActivityInterceptor;
import com.oxandon.mvp.ui.common.ILayout;

/**
 * Created by peng on 2017/5/20.
 */

public interface IFragment extends ILayout, IActivityInterceptor {
    /**
     * 界面可见
     */
    void onVisible();

    /**
     * 界面不可见
     */
    void onInvisible();

    /**
     * 是否可见
     *
     * @return
     */
    boolean visible();
}