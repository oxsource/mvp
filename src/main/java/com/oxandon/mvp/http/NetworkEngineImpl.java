package com.oxandon.mvp.http;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.oxandon.mvp.env.FoundEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 基于OkHttp的请求实现类
 *
 * @author FengPeng
 * @date 2016/11/28
 */
public class NetworkEngineImpl implements INetworkEngine {
    /*主机地址*/
    private String host;
    private OkHttpClient client;
    private Retrofit retrofit;

    @Override
    public final INetworkEngine host(String host) {
        if (TextUtils.isEmpty(host)) {
            throw new IllegalArgumentException("host address can't be empty");
        }
        this.host = host;
        return this;
    }

    @Override
    public final void notifyHostChanged() {
        /*构建OK_HTTP_CLIENT*/
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (FoundEnvironment.isDebug()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addNetworkInterceptor(loggingInterceptor);
        }
        for (Interceptor e : interceptors()) {
            clientBuilder.addInterceptor(e);
        }
        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(60, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(true);
        clientBuilder = onHttpClientBuild(clientBuilder);
        client = clientBuilder.build();
        /*构建RETROFIT适配器*/
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();
        FastJsonConverterFactory converter = converterFactory();
        /*构建RETROFIT*/
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.addCallAdapterFactory(callAdapter).addConverterFactory(converter);
        retrofitBuilder = onRetrofitBuild(retrofitBuilder);
        retrofitBuilder.baseUrl(host()).client(client);
        retrofit = retrofitBuilder.build();
    }

    @NonNull
    @Override
    public List<Interceptor> interceptors() {
        return new ArrayList<>();
    }

    @Override
    public final String host() {
        return host;
    }

    @Override
    public <T> T http(Class<T> service) {
        if (null == retrofit) {
            throw new IllegalStateException("Network engine not in effect");
        }
        return retrofit.create(service);
    }

    @Override
    public OkHttpClient client() {
        return client;
    }

    /**
     * 自定义配置Json转换器
     *
     * @return
     */
    @NonNull
    protected FastJsonConverterFactory converterFactory() {
        FastJsonConverterFactory converter = FastJsonConverterFactory.create();
        return converter;
    }

    /**
     * 自定义配置
     *
     * @param builder
     * @return
     */
    protected OkHttpClient.Builder onHttpClientBuild(final OkHttpClient.Builder builder) {
        return builder;
    }

    /**
     * 自定义配置Retrofit
     *
     * @param builder
     * @return
     */
    protected Retrofit.Builder onRetrofitBuild(final Retrofit.Builder builder) {
        return builder;
    }
}
