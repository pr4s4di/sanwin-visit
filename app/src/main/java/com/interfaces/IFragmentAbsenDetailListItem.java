package com.interfaces;

import android.view.View;

public interface IFragmentAbsenDetailListItem {

    public interface IPresenter
    {

        String doGetBarangBySelectedKode(String kodeBarang);

        String doSaveListBarang(String idKunjungan, String kodeBarang, String namaBarang, String qty, String kodeUser);

        String doGetAllBarangByIdKunjungan(String idKunjungan);

        String doDeleteBarangByIdBarang(String selectedBarang, String kodeKunjungan);
    }

    public interface IView extends View.OnClickListener, IAsyncTaskListener, IViewHolderListener
    {

    }
}
