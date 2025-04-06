package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IActivityListKunjungan;
import com.interfaces.IAsyncTaskListener;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.util.HashMap;

public class PresenterActivityListKunjungan implements IActivityListKunjungan.IPresenter {

    private IActivityListKunjungan.IView view;

    public PresenterActivityListKunjungan(IActivityListKunjungan.IView view) {
        this.view = view;
    }

    @Override
    public String getDataByKodeUser(String kodeUser) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_KUNJUNGAN);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("filter", "kduser");
        params.put("filtervalue", kodeUser);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
