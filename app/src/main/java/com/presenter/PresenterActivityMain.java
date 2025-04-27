package com.presenter;

import android.location.Address;
import android.util.Base64;
import android.util.Log;

import com.enums.EnumRequestType;
import com.interfaces.IActivityMain;
import com.models.ModelSaveAbsensi;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;

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
    public void setImageByteArray(byte[] imageByteArray) {
        // 1. Add null/empty check for robustness
        if (imageByteArray == null || imageByteArray.length == 0) {
            Log.w("Presenter", "setImageByteArray received null or empty byte array.");
            // Optionally set a default/empty value in the model or just return
            // modelSaveAbsensi.setPathPhoto(null); // Or "" depending on your model's needs
            return;
        }

        try {
            // 2. Directly encode the received byte array to Base64
            String base64ImageString = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

            // 3. Set the Base64 string to the model object
            if (modelSaveAbsensi != null) {
                modelSaveAbsensi.setPathPhoto(base64ImageString);
                Log.d("Presenter", "Base64 image string set in model.");
            } else {
                Log.e("Presenter", "modelSaveAbsensi is null. Cannot set photo path.");
                // Handle the case where the model is not initialized
            }

        } catch (Exception e) {
            // Catch potential exceptions during encoding or setting, although Base64 encoding itself rarely throws
            Log.e("Presenter", "Error setting image byte array to model", e);
            // Handle error appropriately
        } catch (OutOfMemoryError e) {
            // Base64 encoding can consume memory, especially for large byte arrays
            Log.e("Presenter", "OutOfMemoryError during Base64 encoding", e);
            // Handle OOM error
        }
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
