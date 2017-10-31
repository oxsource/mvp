package com.oxandon.mvp.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oxandon.mvp.arch.impl.MvpMessage;
import com.oxandon.mvp.arch.impl.MvpSdk;
import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpView;
import com.oxandon.mvp.env.MvpEvent;
import com.oxandon.mvp.ui.activity.MvpActivity;
import com.oxandon.mvp.ui.widget.AlertTemple;
import com.oxandon.mvp.ui.widget.IHintView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by peng on 2017/5/22.
 */

public abstract class MvpFragment extends Fragment implements IFragment, IMvpView {
    private ViewGroup layout;
    private IHintView iHintView;
    private FragmentVisibility visibility;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iHintView = onBuildHintView();
        visibility().onCreate(savedInstanceState);
        getFoundActivity().addToInterceptor(this);
        getEventBus().register(this);
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = onInflateLayout(inflater, container, savedInstanceState);
        if (null != MvpSdk.dispatcher()) {
            MvpSdk.dispatcher().attach(this);
        }
        return layout;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        visibility().onResume();
    }

    @CallSuper
    @Override
    public void onPause() {
        visibility().onPause();
        super.onPause();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != MvpSdk.dispatcher()) {
            MvpSdk.dispatcher().detach(this);
        }
        layout = null;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        getFoundActivity().removeFromInterceptor(this);
        visibility().destroy();
        getEventBus().unregister(this);
        super.onDestroy();
    }

    protected FragmentVisibility visibility() {
        if (null == visibility) {
            visibility = new FragmentVisibility(this);
        }
        return visibility;
    }

    @Override
    public ViewGroup getLayout() {
        return layout;
    }

    @Override
    public IHintView getHintView() {
        return iHintView;
    }

    /*对用户可见时的回调*/
    @Override
    public void onVisible() {
    }

    /*对用户不可见时的回调*/
    @Override
    public void onInvisible() {
    }

    @Override
    public final boolean visible() {
        return visibility().visible();
    }

    /**
     * 在ViewPager中使用
     *
     * @param isVisibleToUser
     */
    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visibility().setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 使用hide,replace方式
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        visibility().onHiddenChanged(hidden);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        visibility().onSaveInstanceState(outState);
    }

    @Override
    public boolean backPressed() {
        return false;
    }

    @Override
    public final MvpFragment fragment() {
        return this;
    }

    @Override
    public boolean function(IMvpMessage msg) {
        MvpMessage.Builder builder = null;
        try {
            if (null == MvpSdk.dispatcher()) throw new IllegalStateException("");
            MvpSdk.dispatcher().dispatchToPresenter(msg);
        } catch (Exception e) {
            e.printStackTrace();
            builder = new MvpMessage.Builder().clone(msg).msg(e.getMessage());
            MvpEvent.exceptCast(builder.build(), e);
        }
        return null == builder;
    }

    @Override
    public boolean dispatch(IMvpMessage msg) {
        String path = msg.to().path();
        switch (msg.what()) {
            case IMvpMessage.WHAT_START:
                onMvpStart(msg, path);
                break;
            case IMvpMessage.WHAT_FINISH:
                onMvpFinish(msg, path);
                break;
            case IMvpMessage.WHAT_PROGRESS:
                onMvpProgress(msg, path);
                break;
            case IMvpMessage.WHAT_FAILURE:
                onMvpFailure(msg, path);
                break;
            case IMvpMessage.WHAT_SUCCESS:
                onMvpSuccess(msg, path);
                break;
            case IMvpMessage.WHAT_PERMISSION:
                onMvpPermission(msg, path);
                break;
        }
        return false;
    }

    protected void onMvpStart(final IMvpMessage msg, String path) {
        String loading = msg.from().getParams(STR_LOADING, "");
        if (!TextUtils.isEmpty(loading)) {
            boolean cancel = msg.from().getParams(BOOL_LOADING, false);
            DialogInterface.OnCancelListener listener = !cancel ? null : dialog -> {
                IMvpMessage cancelMsg = new MvpMessage.Builder()
                        .clone(msg)
                        .what(IMvpMessage.WHAT_FINISH)
                        .build();
                function(cancelMsg);

            };
            getHintView().showLoading(loading, listener);
        }
    }

    @CallSuper
    protected void onMvpFinish(final IMvpMessage msg, String path) {
        String loading = msg.from().getParams(STR_LOADING, "");
        if (!TextUtils.isEmpty(loading)) {
            getHintView().hideLoading();
        }
    }

    protected void onMvpProgress(final IMvpMessage msg, String path) {
    }

    protected void onMvpFailure(final IMvpMessage msg, String path) {
        String errMsg = TextUtils.isEmpty(msg.msg()) ? "请求出错啦" : msg.msg();
        getHintView().showToast(errMsg, 0);
    }

    protected void onMvpSuccess(final IMvpMessage msg, String path) {

    }

    protected void onMvpPermission(final IMvpMessage msg, String path) {
        AlertTemple alert = new AlertTemple("提示", msg.msg());
        alert.setPositiveClick(v -> {
            MvpMessage.Builder builder = new MvpMessage.Builder();
            function(builder.clone(msg).build());
        });
        getHintView().showAlert(alert, false);
    }

    protected final EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @CallSuper
    public void onEventBus(IMvpMessage msg) {
        if (msg.obj() instanceof Exception) {
            String path = msg.to().path();
            onMvpFinish(msg, path);
            onMvpFailure(msg, path);
        }
    }

    protected final MvpActivity getFoundActivity() {
        if (!(getActivity() instanceof MvpActivity)) {
            throw new IllegalStateException("FoundFragment must bind to FoundActivity");
        }
        return (MvpActivity) getActivity();
    }
}