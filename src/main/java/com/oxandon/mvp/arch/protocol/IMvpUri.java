package com.oxandon.mvp.arch.protocol;

/**
 * Created by peng on 2017/5/20.
 */

public interface IMvpUri extends IMvp {

    /**
     * 路径
     *
     * @return
     */
    String path();

    <T> void appendParams(String key, T params);

    <T> T getParams(String key, T value);

    void clear();
}