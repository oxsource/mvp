package com.oxandon.mvp.env;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.meituan.android.walle.WalleChannelReader;
import com.oxandon.mvp.log.FoundLog;
import com.oxandon.mvp.log.MvpLogHandler;

/**
 * Created by peng on 2017/5/20.
 */

public class FoundEnvironment {
    private static Application application;
    private static String nameOfEnvironment;
    private static boolean isDebug;
    private static MvpLogHandler logHandler;

    /**
     * 注入应用程序
     *
     * @param application
     * @param debug
     */
    public static void inject(@NonNull Application application, boolean debug) {
        FoundEnvironment.application = application;
        isDebug = debug;
        String packageName = application.getPackageName();
        nameOfEnvironment = packageName.replaceAll("\\.", "_");
        FoundLog.d("FoundEnvironment inject Application=" + nameOfEnvironment);
    }

    /**
     * 获取应用程序实例
     *
     * @return
     */
    public static Application getApplication() {
        return application;
    }

    /**
     * 获取环境名称
     *
     * @return
     */
    public static String environmentName() {
        return nameOfEnvironment;
    }

    /**
     * 获取渠道名称，必须在注入之后才能调用，否则会抛出异常
     *
     * @return
     */
    public static String getChannel() {
        if (null == getApplication()) {
            throw new IllegalStateException("FoundEnvironment need inject Application!");
        }
        String defaultChannel = isDebug() ? "ALPHA" : "UNKNOWN_CHANNEL";
        return WalleChannelReader.getChannel(getApplication(), defaultChannel);
    }

    /**
     * 是否为调试模式
     *
     * @return
     */
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 获取应用版本号
     *
     * @return
     */
    public static int versionCode() {
        try {
            PackageManager manager = getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplication().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取应用版本名称
     *
     * @return
     */
    public static String versionName() {
        try {
            PackageManager manager = getApplication().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplication().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 应用程序重启
     */
    public static void restart() {
        PackageManager pm = getApplication().getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getApplication().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplication().startActivity(intent);
    }

    /**
     * 日志回调
     *
     * @param handler
     */
    public static void setLogHandler(MvpLogHandler handler) {
        logHandler = handler;
    }

    //检查是否存在指定包名
    public static boolean existPackage(String pkg) {
        try {
            PackageManager pm = getApplication().getPackageManager();
            pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void destroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void log(String log) {
        if (null != logHandler) {
            logHandler.log(log);
        }
    }

    public static void bug(Exception e) {
        String error = "程序运行异常";
        if (null != e) {
            error = e.getMessage();
        }
        Toast.makeText(FoundEnvironment.getApplication(), error, Toast.LENGTH_SHORT).show();
        if (null != logHandler) {
            logHandler.bug(e);
        }
        FoundEnvironment.destroy();
    }
}