package com.interfaces;

import android.location.Address;
import android.view.View;

import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;

/**
 * Created by Alvin on 10/28/2018.
 */
public interface IActivityMain {
    public interface IPresenter
    {
        String doSaveAbsensi();
        void setLatitude(double latitude);
        void setLongitude(double longitude);

        void setImageByteArray(byte[] imageByteArray);
        String doFindUserByKodeUser(String trim);
        void setStatusUser(String keluar);
        void setKodeUser(String kode);
        void setNamaUser(String nama);
        void resetModelSaveAbsensi();
        void setAddress(Address address);
    }

    public interface IView extends View.OnClickListener, OnCompleteListener<LocationSettingsResponse>, IAsyncTaskListener, View.OnKeyListener, View.OnFocusChangeListener
    {

    }
}
