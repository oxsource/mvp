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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * IMvpPresenter的默认实现
 * Created by peng on 2017/5/20.
 */
public class MvpPresenter implements IMvpPresenter {
    private static long LAST_REPEAT_MS = 2000;
    private Map<String, Method> services = new HashMap<>();
    private IMvpDispatcher dispatcher;
    private final List<IMvpMessage> taskQueue = new ArrayList<>();
    private Map<String, Disposable> disposables = new LinkedHashMap<>();
    private long lastRepeatMs = 0;

    public MvpPresenter(IMvpDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            RequestMapping mapping = m.getAnnotation(RequestMapping.class);
            if (null != mapping) {
                services.put(mapping.value(), m);
            }
        }
    }

    public List<IMvpMessage> getTaskQueue() {
        return taskQueue;
    }

    @NonNull
    protected String whenRepeatNote() {
        return "请求太快啦";
    }

    @CallSuper
    @Override
    public boolean onIntercept(IMvpMessage msg) throws Exception {
        IMvpUri uri = msg.to();
        boolean check = uri.getParams(REPEAT_CHECK, true);
        if (!check) {
            return false;
        }
        String path = msg.to().path();
        boolean repeat = disposables.containsKey(path);
        if (!repeat) {
            return false;
        }
        if (msg.from().getParams(REPEAT_QUEUE, false)) {
            if (taskQueue.contains(msg)) {
                taskQueue.remove(msg);
            }
            taskQueue.add(msg);
        } else {
            long ms = System.currentTimeMillis();
            if ((ms - lastRepeatMs) > LAST_REPEAT_MS) {
                catchException(msg, whenRepeatNote());
            }
            lastRepeatMs = ms;
        }
        FoundLog.d("Request too soon!");
        return true;
    }

    @Override
    public boolean distribute(IMvpMessage msg) throws Exception {
        if (msg.what() == IMvpMessage.WHAT_FINISH) {
            //请求取消
            removeTask(msg);
            return true;
        }
        //发起请求
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
            disposables.put(path, null);
            method.invoke(this, msg);
            return true;
        } catch (Exception e) {
            removeTask(msg);
            e.printStackTrace();
            catchException(msg, "500 service error");
        }
        return false;
    }

    @Override
    public void removeTask(IMvpMessage message) {
        if (null != message && null != message.to()) {
            String path = message.to().path();
            if (disposables.containsKey(path)) {
                Disposable disposable = disposables.get(path);
                if (null != disposable && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                disposables.remove(path);
            }
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
        Iterator<Map.Entry<String, Disposable>> iterator = disposables.entrySet().iterator();
        while (iterator.hasNext()) {
            Disposable disposable = iterator.next().getValue();
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        disposables.clear();
        services.clear();
        taskQueue.clear();
        //防止双向引用
        dispatcher = null;
    }

    protected <T> void doRxSubscribe(@NonNull Flowable<T> flow, @NonNull MvpSubscriber<T> subscriber) {
        String path = subscriber.message().to().path();
        flow.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        disposables.put(path, subscriber);
    }

    protected void catchException(IMvpMessage msg, String text) {
        MvpMessage.Builder builder = new MvpMessage.Builder();
        builder.clone(msg).what(IMvpMessage.WHAT_FAILURE).msg(text);
        dispatcher().dispatchToView(builder.build());
    }

    /**
     * 检查参数，如果illegal为TRUE则抛出CheckArgumentException
     */
    protected void checkArgument(boolean illegal, @NonNull String defaultMsg) throws CheckArgumentException {
        if (illegal) {
            throw new CheckArgumentException(defaultMsg);
        }
    }
}