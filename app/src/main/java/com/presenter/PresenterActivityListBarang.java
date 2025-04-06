package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IActivityListBarang;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.util.HashMap;

public class PresenterActivityListBarang implements IActivityListBarang.IPresenter {
    private IActivityListBarang.IView view;

    public PresenterActivityListBarang(IActivityListBarang.IView view) {
        this.view = view;
    }

    @Override
    public String getListBarang() {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_LIST_BARANG);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("filter", "");
        params.put("filtervalue", "");
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
