package com.oxandon.mvp.ui.activity;

import com.oxandon.mvp.ui.common.ILayout;

/**
 * Created by peng on 2017/5/20.
 */

public interface IActivity extends ILayout {

    void addToInterceptor(IActivityInterceptor fragment);

    void removeFromInterceptor(IActivityInterceptor fragment);
}
