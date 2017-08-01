package com.oxandon.mvp.ui.widget;

import android.view.View;

/**
 * Alert对话框模板
 *
 * @author FengPeng
 * @date 2016/11/27
 */
public final class AlertTemple {
    private CharSequence title;
    private CharSequence message;
    private CharSequence negativeText;
    private CharSequence positiveText;

    private View.OnClickListener negativeClick;
    private View.OnClickListener positiveClick;

    public AlertTemple() {
        this("");
    }

    public AlertTemple(CharSequence message) {
        this("提示", message);
    }

    public AlertTemple(CharSequence title, CharSequence message) {
        this.title = title;
        this.message = message;
        negativeText = "取消";
        positiveText = "确定";
    }

    public CharSequence title() {
        return title;
    }

    public CharSequence message() {
        return message;
    }

    public View.OnClickListener onNegtiveClick() {
        return negativeClick;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    public void setNegativeText(CharSequence negativeText) {
        this.negativeText = negativeText;
    }

    public void setPositiveText(CharSequence positiveText) {
        this.positiveText = positiveText;
    }

    public void setNegativeClick(View.OnClickListener negativeClick) {
        this.negativeClick = negativeClick;
    }

    public void setPositiveClick(View.OnClickListener positiveClick) {
        this.positiveClick = positiveClick;
    }

    public View.OnClickListener onPositiveClick() {
        return positiveClick;
    }

    public CharSequence negativeText() {
        return negativeText;
    }

    public CharSequence positiveText() {
        return positiveText;
    }
}