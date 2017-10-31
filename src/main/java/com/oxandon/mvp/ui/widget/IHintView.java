package com.oxandon.mvp.ui.widget;

import android.content.DialogInterface;

/**
 * Created by peng on 2017/5/22.
 */

public interface IHintView {
    void showLoading(CharSequence sequence, DialogInterface.OnCancelListener listener);

    void hideLoading();

    void showAlert(AlertTemple iAlertTemple, boolean cancel);

    void hideAlert();

    void showToast(CharSequence sequence, int what);
}