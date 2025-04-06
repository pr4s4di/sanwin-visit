package com.interfaces;

import android.os.AsyncTask;
import android.view.View;

import com.presenter.PresenterActivitySplashScreen;

import java.io.OutputStream;

public interface IActivitySplashScreen {

    public interface IView extends IAsyncTaskListener, View.OnClickListener{

        void toggleUpdateProgress(boolean b);

        void updateDownloadProgress(Integer[] values);

        void openInstalledIntent(String s);

        OutputStream openFileOutputAsync(String path);
    }

    public interface IPresenter {
        
        String doLogin(String username, String password);
        String getServerVersion();

        AsyncTask getServerApk();
    }
}
