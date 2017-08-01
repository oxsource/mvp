package com.oxandon.mvp.except;

/**
 * 检查参数异常
 * Created by peng on 2017/1/19.
 */
public class CheckArgumentException extends IllegalArgumentException {

    public CheckArgumentException(String message) {
        super(message);
    }
}
