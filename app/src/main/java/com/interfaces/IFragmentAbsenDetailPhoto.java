package com.interfaces;

import android.view.View;

public interface IFragmentAbsenDetailPhoto {

    public interface IPresenter
    {

        String doGetPhotoData(String idKunjungan);

        String doCheckOut(String idKunjungan);
    }

    public interface IView extends IAsyncTaskListener, View.OnClickListener
    {

    }
}
