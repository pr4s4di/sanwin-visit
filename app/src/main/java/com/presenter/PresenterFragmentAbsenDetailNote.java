package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IFragmentAbsenDetailNote;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.net.URL;
import java.util.HashMap;

public class PresenterFragmentAbsenDetailNote implements IFragmentAbsenDetailNote.IPresenter {

    private IFragmentAbsenDetailNote.IView view;
    public PresenterFragmentAbsenDetailNote(IFragmentAbsenDetailNote.IView view) {
        this.view = view;
    }

    @Override
    public String doSaveAbsensiDetail(String kdkunjungan, String note1, String note2, String note3, String kodeUser) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_SAVE_ABSENSI_DETAIL);
        HashMap<String, String> params = new HashMap<>();
        params.put("status", "savenote");
        params.put("kdkunjungan", kdkunjungan);
        params.put("kduser", kodeUser);
        params.put("note1", note1);
        params.put("note2", note2);
        params.put("note3", note3);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public String getAbsensiDataDetail(String kdkunjungan) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_KUNJUNGAN_NOTE);
        HashMap<String, String> params = new HashMap<>();
        params.put("kdkunjungan", kdkunjungan);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
