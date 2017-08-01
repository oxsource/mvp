package com.oxandon.mvp.arch.impl;

import android.support.annotation.MainThread;

import com.oxandon.mvp.arch.protocol.IMvpDispatcher;

/**
 * Created by peng on 2017/5/24.
 */

public class MvpSdk {
    private static IMvpDispatcher instance;
    private static Class<? extends IMvpDispatcher> clazz;

    private MvpSdk() {
    }

    public static void bind(Class<? extends IMvpDispatcher> clazz) {
        MvpSdk.clazz = clazz;
    }

    @MainThread
    public static IMvpDispatcher dispatcher() {
        if (null == clazz) {
            throw new IllegalStateException("please bind IMvpDispatcher's implements clazz");
        }
        if (null == instance) {
            try {
                instance = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}