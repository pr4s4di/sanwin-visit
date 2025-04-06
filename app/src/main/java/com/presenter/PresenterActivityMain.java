package com.presenter;

import android.graphics.Bitmap;
import android.location.Address;
import android.util.Base64;
import android.util.Log;

import com.enums.EnumRequestType;
import com.interfaces.IActivityMain;
import com.models.ModelSaveAbsensi;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by Alvin on 10/28/2018.
 */
public class PresenterActivityMain implements IActivityMain.IPresenter {

    private IActivityMain.IView view;
    private ModelSaveAbsensi modelSaveAbsensi;

    public PresenterActivityMain(IActivityMain.IView view) {
        this.view = view;
        modelSaveAbsensi = new ModelSaveAbsensi();
        resetModelSaveAbsensi();
    }

    @Override
    public String doSaveAbsensi() {
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_SAVE_ABSENSI);
        HashMap<String, String> params = new HashMap<>();
        params.put("kduser", modelSaveAbsensi.getKodeUser());
        params.put("nama", modelSaveAbsensi.getNama());
        params.put("latitude", String.valueOf(modelSaveAbsensi.getLatitude()));
        params.put("longitude", String.valueOf(modelSaveAbsensi.getLongitude()));
        params.put("status", modelSaveAbsensi.getStatusUser());
        params.put("nmfoto", modelSaveAbsensi.getPathPhoto());
        params.put("alamat", modelSaveAbsensi.getAlamat());

        ConnectionUtils connectionUtils = new ConnectionUtils();
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public void setLatitude(double latitude) {
        modelSaveAbsensi.setLatitude(latitude);
    }

    @Override
    public void setLongitude(double longitude) {
        modelSaveAbsensi.setLongitude(longitude);
    }

    @Override
    public void setImageByteArray(Bitmap bitmapCapturedImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapCapturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] streamByteArray = stream.toByteArray();
        modelSaveAbsensi.setPathPhoto(Base64.encodeToString(streamByteArray, Base64.DEFAULT));

    }

    @Override
    public String doFindUserByKodeUser(String trim) {
        HashMap<String, String> params = new HashMap<>();
        params.put("kduser", trim);
        String url = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_GET_USER_BY_KODE_USER);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        return connectionUtils.sendRequest(EnumRequestType.POST, url, params);
    }

    @Override
    public void setStatusUser(String status) {
        modelSaveAbsensi.setStatusUser(status);
    }

    @Override
    public void setKodeUser(String kode) {
        modelSaveAbsensi.setKodeUser(kode);
    }

    @Override
    public void setNamaUser(String nama) {
        modelSaveAbsensi.setNama(nama);
    }

    @Override
    public void resetModelSaveAbsensi() {
/*        modelSaveAbsensi.setNama("");
        modelSaveAbsensi.setKodeUser("");
        modelSaveAbsensi.setLongitude(0.0);
        modelSaveAbsensi.setLatitude(0.0);
        modelSaveAbsensi.setAlamat("");*/
        modelSaveAbsensi.setStatusUser("");
        modelSaveAbsensi.setPathPhoto("");
    }

    @Override
    public void setAddress(Address address) {
        modelSaveAbsensi.setAlamat(address.getAddressLine(0));
        Log.d("Alamat", address.getAddressLine(0));
    }
}
