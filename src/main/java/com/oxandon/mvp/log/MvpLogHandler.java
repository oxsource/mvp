package com.oxandon.mvp.log;

/**
 * Created by peng on 2017/9/20.
 */

public interface MvpLogHandler {
    void log(String log);

    void bug(Exception e);
}