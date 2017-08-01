package com.oxandon.mvp.arch.impl;

import com.oxandon.mvp.arch.protocol.IMvpRepository;
import com.oxandon.mvp.parcel.IParcelFormat;
import com.oxandon.mvp.parcel.ParcelFormatImpl;

/**
 * Created by peng on 2017/5/23.
 */

public class MvpRepository implements IMvpRepository {

    private IParcelFormat parcelFormat;

    public MvpRepository() {
        parcelFormat = new ParcelFormatImpl();
    }

    @Override
    public String authority() {
        return getClass().getName();
    }

    @Override
    public IParcelFormat getParcel() {
        return parcelFormat;
    }
}