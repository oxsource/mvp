package com.oxandon.mvp.arch.protocol;

import com.oxandon.mvp.parcel.IParcelFormat;

/**
 * Created by peng on 2017/5/23.
 */

public interface IMvpRepository extends IMvp {
    IParcelFormat getParcel();
}
