package com.oxandon.mvp.arch.impl;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpView;
import com.oxandon.mvp.env.FoundEnvironment;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by peng on 2017/5/22.
 */

public class MvpSubscriber<T> extends DisposableSubscriber<T> {
    private MvpPresenter presenter;
    private IMvpMessage message;

    public MvpSubscriber(@NonNull MvpPresenter presenter, @NonNull IMvpMessage message) {
        this.presenter = presenter;
        this.message = message;
    }

    public MvpSubscriber(@NonNull MvpPresenter presenter, @NonNull IMvpMessage message, String loading, boolean cancel) {
        message.from().appendParams(IMvpView.STR_LOADING, loading);
        message.from().appendParams(IMvpView.BOOL_LOADING, cancel);
        this.presenter = presenter;
        this.message = message;
    }

    @Override
    protected void onStart() {
        super.onStart();
        MvpMessage.Builder builder = new MvpMessage.Builder();
        IMvpMessage msg = builder.reverse(message()).what(IMvpMessage.WHAT_START).build();
        try {
            presenter().dispatcher().dispatchToView(msg);
        } catch (Exception e) {
            FoundEnvironment.bug(e);
        }
    }

    @CallSuper
    @Override
    public void onNext(T o) {
        sendFinishMsg();
    }

    @CallSuper
    @Override
    public void onError(Throwable t) {
        sendFinishMsg();
        String text = defaultErrorMsg();
        if (null != t && !TextUtils.isEmpty(t.getMessage())) {
            text = t.getMessage();
        }
        MvpMessage.Builder builder = new MvpMessage.Builder();
        builder.reverse(message()).what(IMvpMessage.WHAT_FAILURE).msg(text).obj(t);
        try {
            presenter().dispatcher().dispatchToView(builder.build());
        } catch (Exception e) {
            FoundEnvironment.bug(e);
        }
        doFinishedWork();
    }

    @Override
    public void onComplete() {
        doFinishedWork();
    }

    private void sendFinishMsg() {
        MvpMessage.Builder builder = new MvpMessage.Builder();
        IMvpMessage msg = builder.reverse(message()).what(IMvpMessage.WHAT_FINISH).build();
        try {
            presenter().dispatcher().dispatchToView(msg);
        } catch (Exception e) {
            FoundEnvironment.bug(e);
        }
    }

    protected void doFinishedWork() {
        presenter().removeTask(message());
        message = null;
        presenter = null;
    }

    protected String defaultErrorMsg() {
        return "请求出错";
    }

    protected MvpPresenter presenter() {
        return presenter;
    }

    protected IMvpMessage message() {
        return message;
    }
}