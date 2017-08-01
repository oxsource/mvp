package com.oxandon.mvp.http;

import android.support.annotation.NonNull;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 功能：网络引擎接口
 *
 * @author FengPeng
 * @date 2016/11/28
 */
public interface INetworkEngine {
    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    INetworkEngine host(String host);

    /**
     * 通知主机地址修改
     */
    void notifyHostChanged();

    /**
     * 设置拦截器
     *
     * @return
     */
    @NonNull
    List<Interceptor> interceptors();

    /**
     * 获取当前主机地址
     *
     * @return
     */
    String host();

    /**
     * HTTP网络请求
     *
     * @param service HTTP服务接口
     * @param <T>     HTTP服务实例
     * @return
     */
    <T> T http(Class<T> service);

    /**
     * 获取OkHttpClient
     *
     * @return
     */
    OkHttpClient client();
}