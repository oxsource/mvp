package com.oxandon.mvp.env;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.oxandon.mvp.arch.impl.MvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpMessage;
import com.oxandon.mvp.arch.protocol.IMvpUri;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by peng on 2017/5/23.
 */

public class MvpEvent {
    /**
     * 单播
     *
     * @param uri
     * @param obj
     */
    public static void singleCast(IMvpUri uri, Object obj) {
        MvpMessage.Builder builder = new MvpMessage.Builder();
        builder.to(uri).obj(obj);
        EventBus.getDefault().post(builder.build());
    }

    /**
     * 广播
     *
     * @param uri
     * @param obj
     */
    public static void multiCast(IMvpUri uri, Object obj) {
        MvpMessage.Builder builder = new MvpMessage.Builder();
        builder.from(uri).obj(obj);
        EventBus.getDefault().post(builder.build());
    }

    /**
     * 异常广播
     *
     * @param msg
     * @param e
     */
    public static void exceptCast(IMvpMessage msg, Exception e) {
        FoundEnvironment.bug(e);
        IMvpMessage except = new MvpMessage.Builder().clone(msg).obj(e).build();
        EventBus.getDefault().post(except);
    }

    public static boolean singleCast(IMvpMessage message, @NonNull String authority) {
        if (null == message || null == message.to()) {
            return false;
        }
        return authority.equals(message.to().authority());
    }

    public static boolean multiCast(IMvpMessage message, @NonNull String authority) {
        if (null == message || null == message.from()) {
            return false;
        }
        return authority.equals(message.from().authority());
    }

    public static String catchException(Throwable e, String split, String msg) {
        if (null != e && !TextUtils.isEmpty(e.getMessage())) {
            msg = split + e.getMessage();
        }
        return msg;
    }
}