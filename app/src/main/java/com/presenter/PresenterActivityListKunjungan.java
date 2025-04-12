package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IActivityListKunjungan;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.util.HashMap;

public class PresenterActivityListKunjungan implements IActivityListKunjungan.IPresenter {

    private IActivityListKunjungan.IView view;

    public PresenterActivityListKunjungan(IActivityListKunjungan.IView view) {
        this.view = view;
    }

    @Override
    public String getDataByKodeUser(String kodeUser, int page) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_KUNJUNGAN);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("filter", "kduser");
        params.put("filtervalue", kodeUser);
        params.put("page", String.valueOf(page));
        params.put("pagesize", "10");
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
