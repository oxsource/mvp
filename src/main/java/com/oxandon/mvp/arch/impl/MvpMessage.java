package com.oxandon.mvp.arch.impl;

import android.support.annotation.NonNull;

import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpUri;

/**
 * IMvpMessage的默认实现
 * Created by peng on 2017/5/20.
 */

public final class MvpMessage implements IMvpMessage {
    private int what;
    private String msg;
    private Object obj;
    private IMvpUri from;
    private IMvpUri to;

    @Override
    public String authority() {
        return toString();
    }

    public static class Builder {
        private int what;
        private String msg;
        private Object obj;
        private IMvpUri from;
        private IMvpUri to;

        public Builder what(int what) {
            this.what = what;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder obj(Object obj) {
            this.obj = obj;
            return this;
        }

        public Builder from(IMvpUri from) {
            this.from = from;
            return this;
        }

        public Builder to(IMvpUri to) {
            this.to = to;
            return this;
        }

        public Builder reverse(@NonNull IMvpMessage message) {
            this.to = message.from();
            this.from = message.to();
            this.what = message.what();
            this.obj = message.obj();
            this.msg = message.msg();
            return this;
        }

        public Builder clone(@NonNull IMvpMessage message) {
            this.to = message.from();
            this.from = message.to();
            this.what = message.what();
            this.obj = message.obj();
            this.msg = message.msg();
            return this;
        }

        public MvpMessage build() {
            return new MvpMessage(this);
        }
    }

    public MvpMessage(Builder builder) {
        this.what = builder.what;
        this.msg = builder.msg;
        this.obj = builder.obj;
        this.from = builder.from;
        this.to = builder.to;
    }

    @Override
    public int what() {
        return what;
    }

    @Override
    public String msg() {
        return msg;
    }

    @Override
    public Object obj() {
        return obj;
    }

    @Override
    public IMvpUri from() {
        return from;
    }

    @Override
    public IMvpUri to() {
        return to;
    }
}