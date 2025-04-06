package com.interfaces;

import android.view.View;

public interface IFragmentAbsenDetailNote {
    public interface IPresenter
    {
        String doSaveAbsensiDetail(String kdkunjungan, String note1, String note2, String note3, String kodeUser);
        String getAbsensiDataDetail(String idKunjungan);
    }

    public interface IView extends View.OnClickListener, IAsyncTaskListener
    {

    }
}
