package com.interfaces;

import android.support.v4.widget.SwipeRefreshLayout;

public interface IActivityListKunjungan {
    public interface IPresenter
    {

        String getDataByKodeUser(String kodeUser, int page);
    }

    public interface IView extends SwipeRefreshLayout.OnRefreshListener, IAsyncTaskListener, IViewHolderListener
    {

    }
}
