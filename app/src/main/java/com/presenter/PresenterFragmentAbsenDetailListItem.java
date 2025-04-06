package com.presenter;

import com.enums.EnumRequestType;
import com.interfaces.IFragmentAbsenDetailListItem;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.util.HashMap;

public class PresenterFragmentAbsenDetailListItem implements IFragmentAbsenDetailListItem.IPresenter {

    private IFragmentAbsenDetailListItem.IView view;

    public PresenterFragmentAbsenDetailListItem(IFragmentAbsenDetailListItem.IView view) {
        this.view = view;
    }

    @Override
    public String doGetBarangBySelectedKode(String kodeBarang) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_LIST_BARANG);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("filter", "kditem");
        params.put("filtervalue", kodeBarang);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public String doSaveListBarang(String idKunjungan, String kodeBarang, String namaBarang, String qty, String kodeUser) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_SAVE_ABSENSI_DETAIL);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("status", "saveorder");
        params.put("kduser", kodeUser);
        params.put("kdkunjungan", idKunjungan);
        params.put("kditem", kodeBarang);
        params.put("nmitem", namaBarang);
        params.put("qtyitem", qty);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public String doGetAllBarangByIdKunjungan(String idKunjungan) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_KUNJUNGAN_LIST_BARANG);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("kdkunjungan", idKunjungan);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public String doDeleteBarangByIdBarang(String selectedBarang, String kodeKunjungan) {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_DELETE_SELECTED_BARANG);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("kditem", selectedBarang);
        params.put("kdkunjungan", kodeKunjungan);
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }
}
