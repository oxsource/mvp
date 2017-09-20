package com.oxandon.mvp.arch.impl;

import android.support.annotation.NonNull;

import com.oxandon.mvp.annotation.Controller;
import com.oxandon.mvp.arch.protocol.IMvpDispatcher;
import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpPresenter;
import com.oxandon.mvp.arch.protocol.IMvpView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IMvpDispatcher的默认实现
 * Created by peng on 2017/5/20.
 */
public class MvpDispatcher implements IMvpDispatcher {
    private final Map<String, Class<? extends IMvpPresenter>> services = new HashMap<>();
    private final Map<String, WeakReference<IMvpView>> views = new HashMap<>();
    private final Map<String, List<IMvpPresenter>> presenters = new HashMap<>();

    public MvpDispatcher() {
        List<Class<? extends IMvpPresenter>> list = support();
        if (null == list || list.size() == 0) {
            return;
        }
        for (Class<? extends IMvpPresenter> clazz : list) {
            Controller controller = clazz.getAnnotation(Controller.class);
            if (null != controller) {
                services.put(controller.value(), clazz);
            }
        }
    }

    @NonNull
    protected List<Class<? extends IMvpPresenter>> support() {
        return new ArrayList<>();
    }

    @Override
    public void attach(IMvpView view) {
        String viewKey = view.authority();
        WeakReference<IMvpView> reference = new WeakReference<>(view);
        views.put(viewKey, reference);
    }

    @Override
    public void detach(IMvpView view) {
        String viewKey = view.authority();
        List<IMvpPresenter> list = presenters.get(viewKey);
        if (null != list) {
            presenters.remove(viewKey);
            for (IMvpPresenter presenter : list) {
                if (null != presenter) {
                    presenter.destroy();
                }
            }
            list.clear();
        }
        views.remove(viewKey);
    }

    @Override
    public boolean dispatchToPresenter(IMvpMessage msg) throws Exception {
        String viewKey = msg.from().authority();
        //验证View是否存在
        WeakReference<IMvpView> reference = views.get(viewKey);
        if (null == reference || null == reference.get()) {
            throw new IllegalStateException("view is null");
        }
        String presenterKey = msg.to().authority();
        Class<? extends IMvpPresenter> clazz = services.get(presenterKey);
        if (null == clazz) {
            throw new IllegalStateException("presenter is null");
        }
        List<IMvpPresenter> list = presenters.get(viewKey);
        list = null == list ? new ArrayList<IMvpPresenter>() : list;
        IMvpPresenter presenter = null;
        for (IMvpPresenter p : list) {
            if (p.getClass() == clazz) {
                presenter = p;
                break;
            }
        }
        if (null == presenter) {
            Constructor<? extends IMvpPresenter> c = clazz.getConstructor(IMvpDispatcher.class);
            presenter = c.newInstance(MvpDispatcher.this);
            list.add(presenter);
            presenters.put(viewKey, list);
        }
        return presenter.distribute(msg);
    }

    @Override
    public boolean dispatchToView(IMvpMessage msg) throws Exception {
        String viewKey = msg.to().authority();
        WeakReference<IMvpView> reference = views.get(viewKey);
        if (null == reference || null == reference.get()) {
            throw new IllegalStateException("view is null");
        }
        IMvpView view = reference.get();
        return view.dispatch(msg);
    }

    @Override
    public Object provideFromView(IMvpMessage msg) throws Exception {
        String viewKey = msg.to().authority();
        WeakReference<IMvpView> reference = views.get(viewKey);
        if (null == reference || null == reference.get()) {
            throw new IllegalStateException("view is null");
        }
        IMvpView view = reference.get();
        Object obj = view.provide(msg);
        return null == obj ? new Object() : obj;
    }

    @Override
    public String authority() {
        return getClass().getName();
    }
}