package com.oxandon.mvp.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.oxandon.mvp.ui.widget.IHintView;

/**
 * Created by peng on 2017/5/22.
 */

public interface ILayout {
    /**
     * 名称
     *
     * @return
     */
    String getName();

    /**
     * 解析布局
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    ViewGroup onInflateLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 视图初始化
     *
     * @param savedInstanceState
     */
    void onInitViews(@Nullable Bundle savedInstanceState);

    /**
     * 构建HintView
     *
     * @return
     */
    IHintView onBuildHintView();

    /**
     * 获取根布局，方便时实现对界面布局动态控制
     *
     * @return
     */
    ViewGroup getLayout();

    /**
     * 创建基础提示View
     */
    IHintView getHintView();
}