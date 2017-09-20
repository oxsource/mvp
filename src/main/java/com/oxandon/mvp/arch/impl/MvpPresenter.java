package com.oxandon.mvp.arch.impl;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.oxandon.mvp.annotation.Controller;
import com.oxandon.mvp.annotation.RequestMapping;
import com.oxandon.mvp.arch.protocol.IMvpDispatcher;
import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpPresenter;
import com.oxandon.mvp.arch.protocol.IMvpUri;
import com.oxandon.mvp.except.CheckArgumentException;
import com.oxandon.mvp.log.FoundLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * IMvpPresenter的默认实现
 * Created by peng on 2017/5/20.
 */
public class MvpPresenter implements IMvpPresenter {
    private Map<String, Method> services = new HashMap<>();
    private List<String> tasking = new ArrayList<>();
    private IMvpDispatcher dispatcher;
    private CompositeDisposable composite;

    public MvpPresenter(IMvpDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            RequestMapping mapping = m.getAnnotation(RequestMapping.class);
            if (null != mapping) {
                services.put(mapping.value(), m);
            }
        }
        composite = new CompositeDisposable();
    }

    @CallSuper
    @Override
    public boolean onIntercept(IMvpMessage msg) throws Exception {
        IMvpUri uri = msg.to();
        boolean check = uri.getParams(REPEAT_CHECK, true);
        String path = msg.to().path();
        boolean repeat = tasking.contains(path);
        if (check && repeat) {
            FoundLog.d("Request too soon!");
            return true;
        }
        return false;
    }

    @Override
    public boolean distribute(IMvpMessage msg) throws Exception {
        boolean intercepted = onIntercept(msg);
        if (intercepted) {
            return false;
        }
        String path = msg.to().path();
        Method method = services.get(path);
        if (null == method) {
            catchException(msg, "404 Not found");
            return false;
        }
        try {
            tasking.add(path);
            method.invoke(this, msg);
            return true;
        } catch (Exception e) {
            removeTask(msg);
            e.printStackTrace();
            catchException(msg, "500 Server error");
        }
        return false;
    }

    @Override
    public void removeTask(IMvpMessage message) {
        if (null != message && null != message.to()) {
            String path = message.to().path();
            tasking.remove(path);
        }
    }

    @Override
    public IMvpDispatcher dispatcher() {
        return dispatcher;
    }

    @Override
    public String authority() {
        Controller controller = getClass().getAnnotation(Controller.class);
        return controller.value();
    }

    @Override
    public void destroy() {
        composite.dispose();
        services.clear();
        tasking.clear();
        //防止双向引用
        dispatcher = null;
    }

    protected IMvpMessage reverse(IMvpMessage msg) {
        return msg;
    }

    protected <T> void doRxSubscribe(@NonNull Flowable<T> flow, @NonNull DisposableSubscriber<T> subscriber) {
        flow.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        composite.add(subscriber);
    }

    protected void catchException(IMvpMessage msg, String text) {
        MvpMessage.Builder builder = new MvpMessage.Builder();
        builder.to(msg.from()).what(IMvpMessage.WHAT_FAILURE).msg(text);
        dispatcher().dispatchToView(builder.build());
    }

    /**
     * 检查参数，如果illegal为TRUE则抛出CheckArgumentException
     *
     * @param illegal
     * @param defaultMsg
     */
    protected void checkArgument(boolean illegal, @NonNull String defaultMsg) throws CheckArgumentException {
        if (illegal) {
            throw new CheckArgumentException(defaultMsg);
        }
    }
}