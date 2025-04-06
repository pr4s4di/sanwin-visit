package com.presenter;

import com.interfaces.IActivityAbsenDetail;

public class PresenterActivityAbsenDetail implements IActivityAbsenDetail.IPresenter {

    private IActivityAbsenDetail.IView view;

    public PresenterActivityAbsenDetail(IActivityAbsenDetail.IView view) {
        this.view = view;
    }
}
