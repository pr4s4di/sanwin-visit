package com.interfaces;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.SearchView;

public interface IActivityListBarang {

    public interface IPresenter {
        String getListBarang();
    }

    public interface IView extends SwipeRefreshLayout.OnRefreshListener, IAsyncTaskListener, IViewHolderListener, android.support.v7.widget.SearchView.OnQueryTextListener{

    }
}
