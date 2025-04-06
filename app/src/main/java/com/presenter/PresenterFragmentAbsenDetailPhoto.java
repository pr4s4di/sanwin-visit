package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IFragmentAbsenDetailPhoto;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.util.HashMap;

public class PresenterFragmentAbsenDetailPhoto implements IFragmentAbsenDetailPhoto.IPresenter {

    private IFragmentAbsenDetailPhoto.IView view;

    public PresenterFragmentAbsenDetailPhoto(IFragmentAbsenDetailPhoto.IView view) {
        this.view = view;
    }

    @Override
    public String doGetPhotoData(String idKunjungan) {
        ConnectionUtils connectionUtils = new ConnectionUtils();
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_KUNJUNGAN);
        HashMap<String, String> params = new HashMap<>();
        params.put("filter", "kdkunjungan");
        params.put("filtervalue", idKunjungan);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public String doCheckOut(String idKunjungan) {
        ConnectionUtils connectionUtils = new ConnectionUtils();
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_CHECKOUT);
        HashMap<String, String> params = new HashMap<>();
        params.put("idkunjungan", idKunjungan);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
