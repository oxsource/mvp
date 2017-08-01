package com.oxandon.mvp.arch.impl;

import com.oxandon.mvp.arch.protocol.IMvpUri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peng on 2017/5/20.
 */

public class MvpUri implements IMvpUri {
    private String authority;
    private String path;
    private Map<String, Object> params = new HashMap<>();

    public MvpUri(String authority, String path) {
        this.authority = authority;
        this.path = path;
    }

    @Override
    public String authority() {
        return authority;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public <T> void appendParams(String key, T obj) {
        params.put(key, obj);
    }

    @Override
    public <T> T getParams(String key, T value) {
        T result = null;
        try {
            result = (T) params.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null == result ? value : result;
    }

    @Override
    public void clear() {
        authority = null;
        path = null;
        params.clear();
    }
}
