package com.oxandon.mvp.parcel;

/**
 * 存在异常的结果
 * Created by peng on 2017/5/22.
 */

public final class ExceptionSupply<T> {
    private Exception e;
    private T supply;

    public ExceptionSupply(Exception e) {
        this.supply = null;
        this.e = e;
    }

    public ExceptionSupply(T value) {
        this.supply = value;
        this.e = null;
    }

    public Exception e() {
        return e;
    }

    public T supply() {
        return supply;
    }
}