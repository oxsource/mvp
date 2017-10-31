package com.oxandon.mvp.http;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.IntDef;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.oxandon.mvp.env.FoundEnvironment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 网络工具类
 * Created by peng on 2017/5/22.
 */

public class NetworkUtil {
    public final static int NETWORK_NONE = 0;
    public final static int NETWORK_WIFI = 1;
    public final static int NETWORK_2G = 2;
    public final static int NETWORK_3G = 3;
    public final static int NETWORK_4G = 4;
    public final static int NETWORK_MOBILE = 5;

    @IntDef(value = {NETWORK_NONE, NETWORK_WIFI, NETWORK_2G, NETWORK_3G, NETWORK_4G, NETWORK_MOBILE})
    @Retention(RetentionPolicy.SOURCE)
    @interface CONNECT {
    }

    /**
     * 获取当前网络连接类型
     *
     * @return
     */
    public static
    @CONNECT
    int getNetworkType() {
        Application context = FoundEnvironment.getApplication();
        Object obj = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager manager = ((ConnectivityManager) obj);
        if (null == manager) {
            return NETWORK_NONE;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return NETWORK_NONE;
        }
        if (ConnectivityManager.TYPE_WIFI == info.getType()) {
            NetworkInfo.State state = info.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
        }
        NetworkInfo.State state = info.getState();
        String strSubTypeName = info.getSubtypeName();
        if (null != state)
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                    case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_2G;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_4G;
                    default:
                        String[] names = new String[]{"TD-SCDMA", "WCDMA", "CDMA2000"};
                        for (String name : names) {
                            if (strSubTypeName.equalsIgnoreCase(name)) {
                                return NETWORK_3G;
                            }
                        }
                        return NETWORK_MOBILE;
                }
            }
        return NETWORK_NONE;
    }

    public static String getIPAddress() {
        Application context = FoundEnvironment.getApplication();
        Object obj = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(obj instanceof ConnectivityManager)) {
            return "";
        }
        NetworkInfo info = ((ConnectivityManager) obj).getActiveNetworkInfo();
        if (null == info || info.isConnected()) {
            return "";
        }
        String ipAddress = null;
        try {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                outer:
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface ntf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddress = ntf.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddress.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            ipAddress = inetAddress.getHostAddress();
                            break outer;
                        }
                    }
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (null != wifiInfo) {
                    ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TextUtils.isEmpty(ipAddress) ? "" : ipAddress;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
