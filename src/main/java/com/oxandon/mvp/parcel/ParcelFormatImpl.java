package com.oxandon.mvp.parcel;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peng on 2017/5/22.
 */

public class ParcelFormatImpl implements IParcelFormat {
    private final String NULL_SUPPLY = "supply is null";

    @Override
    public ExceptionSupply<Map<String, String>> map(@NonNull Object object) {
        ExceptionSupply<Map<String, String>> value;
        try {
            String json = JSON.toJSONString(object);
            Map<String, String> map = JSON.parseObject(json, new TypeReference<Map<String, String>>() {
            });
            value = supply(map);
        } catch (Exception e) {
            value = new ExceptionSupply<>(e);
        }
        return value;
    }

    @Override
    public ExceptionSupply<String> string(@NonNull Object object) {
        ExceptionSupply<String> value;
        try {
            String json = JSON.toJSONString(object);
            value = supply(json);
        } catch (Exception e) {
            value = new ExceptionSupply<>(e);
        }
        return value;
    }

    @Override
    public <T> ExceptionSupply<T> object(String content, @NonNull Class<T> clazz) {
        ExceptionSupply<T> value;
        try {
            T object = JSON.parseObject(content, clazz);
            value = supply(object);
        } catch (Exception e) {
            value = new ExceptionSupply<>(e);
        }
        return value;
    }

    @Override
    public <T> ExceptionSupply<List<T>> objects(String content, @NonNull Class<T> clazz) {
        ExceptionSupply<List<T>> value;
        try {
            List<T> objects = JSON.parseArray(content, clazz);
            value = supply(objects);
        } catch (Exception e) {
            value = new ExceptionSupply<>(e);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> opts(Object object, @NonNull Class<T> clazz) {
        if (!(object instanceof List<?>)) {
            return null;
        }
        List<Object> objList = (List<Object>) object;
        final List<T> list = new ArrayList<>();
        if (objList.size() == 0) {
            return list;
        }
        for (Object e : objList) {
            if (e.getClass() == clazz) {
                list.add((T) e);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T opt(Object object, @NonNull Class<T> clazz) {
        if (null == object) {
            return null;
        }
        T value = null;
        if (object.getClass() == clazz) {
            value = (T) object;
        }
        return value;
    }

    private <T> ExceptionSupply supply(T obj) {
        ExceptionSupply<T> es;
        if (null == obj) {
            es = new ExceptionSupply(new Exception(NULL_SUPPLY));
        } else {
            es = new ExceptionSupply(obj);
        }
        return es;
    }
}