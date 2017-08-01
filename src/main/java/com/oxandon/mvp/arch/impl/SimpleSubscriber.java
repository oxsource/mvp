package com.oxandon.mvp.arch.impl;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by peng on 2017/5/23.
 */

public class SimpleSubscriber<T> extends DisposableSubscriber<T> {

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}