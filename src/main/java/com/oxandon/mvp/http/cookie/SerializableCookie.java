package com.oxandon.mvp.http.cookie;

import okhttp3.Cookie;

/**
 * @author FengPeng
 * @date 2016/12/28
 */
public class SerializableCookie {
    private String name;
    private String value;
    private long expiresAt;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    private boolean persistent; // True if 'expires' or 'max-age' is present.
    private boolean hostOnly; // True unless 'domain' is present.

    public SerializableCookie() {

    }

    public SerializableCookie(Cookie cookie) {
        name = cookie.name();
        value = cookie.value();
        expiresAt = cookie.expiresAt();
        domain = cookie.domain();
        path = cookie.path();
        secure = cookie.secure();
        httpOnly = cookie.httpOnly();

        persistent = cookie.persistent();
        hostOnly = cookie.hostOnly();
    }

    public Cookie transfer() {
        Cookie.Builder builder = new Cookie.Builder();
        return builder.name(name).value(value).expiresAt(expiresAt).domain(domain).build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isHostOnly() {
        return hostOnly;
    }

    public void setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
    }
}
