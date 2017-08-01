package com.oxandon.mvp.parcel;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Created by peng on 2017/5/22.
 */

public interface IParcelFormat {
    ExceptionSupply<Map<String, String>> map(@NonNull Object object);

    ExceptionSupply<String> string(@NonNull Object object);

    <T> ExceptionSupply<T> object(String content, @NonNull Class<T> clazz);

    <T> ExceptionSupply<List<T>> objects(String content, @NonNull Class<T> clazz);

    <T> List<T> opts(Object object, @NonNull Class<T> clazz);

    <T> T opt(Object object, @NonNull Class<T> clazz);
}