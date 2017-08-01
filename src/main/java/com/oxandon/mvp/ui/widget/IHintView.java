package com.oxandon.mvp.ui.widget;

/**
 * Created by peng on 2017/5/22.
 */

public interface IHintView {
    void showLoading(CharSequence sequence, boolean cancel);

    void hideLoading();

    void showAlert(AlertTemple iAlertTemple, boolean cancel);

    void hideAlert();

    void showToast(CharSequence sequence, int what);
}