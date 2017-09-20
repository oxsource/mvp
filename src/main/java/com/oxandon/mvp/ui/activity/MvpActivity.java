package com.oxandon.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.oxandon.mvp.ui.widget.IHintView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peng on 2017/5/22.
 */

public abstract class MvpActivity extends FragmentActivity implements IActivity {
    private ViewGroup layout;
    private IHintView iHintView;
    private List<IActivityInterceptor> activityInterceptors = new ArrayList<>();

    @Override
    public String getName() {
        return "";
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = onInflateLayout(LayoutInflater.from(getBaseContext()), null, savedInstanceState);
        setContentView(layout);
        iHintView = onBuildHintView();
        onViewCreated(savedInstanceState);
        if (null != getEventBus()) {
            getEventBus().register(this);
        }
    }

    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public ViewGroup getLayout() {
        return layout;
    }

    @Override
    public IHintView getHintView() {
        return iHintView;
    }

    private long lastExitMs = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (doubleClickExit() && keyCode == KeyEvent.KEYCODE_BACK) {
            long offset = System.currentTimeMillis() - lastExitMs;
            if (offset > 3 * 1000) {
                lastExitMs += offset;
                getHintView().showToast("再按一次退出程序", 0);
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否需要双击返回退出
     *
     * @return
     */
    protected boolean doubleClickExit() {
        return false;
    }

    @Override
    public final void addToInterceptor(IActivityInterceptor interceptor) {
        if (getSupportFragmentManager() == interceptor.fragment().getFragmentManager()) {
            if (!activityInterceptors.contains(interceptor)) {
                activityInterceptors.add(interceptor);
            }
        }
    }

    @Override
    public final void removeFromInterceptor(IActivityInterceptor interceptor) {
        if (getSupportFragmentManager() == interceptor.fragment().getFragmentManager()) {
            if (activityInterceptors.contains(interceptor)) {
                activityInterceptors.remove(interceptor);
            }
        }
    }

    @Override
    public void onBackPressed() {
        for (IActivityInterceptor interceptor : activityInterceptors) {
            if (interceptor.fragment().isVisible() && interceptor.backPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        activityInterceptors.clear();
        if (null != getEventBus()) {
            getEventBus().unregister(this);
        }
        super.onDestroy();
    }

    protected EventBus getEventBus() {
        return null;
    }
}