package com.oxandon.mvp.arch.protocol;

/**
 * Created by peng on 2017/5/20.
 */

public interface IMvpMessage extends IMvp {
    int WHAT_START = 100;
    int WHAT_SUCCESS = 200;
    int WHAT_FINISH = 400;
    int WHAT_FAILURE = 500;
    int WHAT_PROGRESS = 300;
    int WHAT_PERMISSION = 600;

    int what();

    String msg();

    Object obj();

    IMvpUri from();

    IMvpUri to();
}