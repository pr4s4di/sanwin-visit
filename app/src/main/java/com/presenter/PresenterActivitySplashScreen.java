package com.presenter;

import android.os.AsyncTask;

import com.enums.EnumRequestType;
import com.interfaces.IActivitySplashScreen;
import com.strings.URLCollections;
import com.utils.ConnectionUtils;
import com.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class PresenterActivitySplashScreen implements IActivitySplashScreen.IPresenter {

    private IActivitySplashScreen.IView view;

    public PresenterActivitySplashScreen(IActivitySplashScreen.IView view) {
        this.view = view;
    }

    @Override
    public String doLogin(String username, String password) {
        String link = String.format("%s%s", URLCollections.SERVER, URLCollections.DO_LOGIN);
        ConnectionUtils connectionUtils = new ConnectionUtils();
        HashMap<String, String> params = new HashMap<>();
        params.put("kduser", username);
        params.put("password", password);
        return connectionUtils.sendRequest(EnumRequestType.POST, link, params);
    }

    @Override
    public String getServerVersion() {
        StringBuilder sb = new StringBuilder();
        String link = String.format("%s%s", URLCollections.SERVER, URLCollections.VERSION);
        URL url = null;
        try {
            url = new URL(link);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public AsyncTask getServerApk() {
        class AsyncTaskDownloadApk extends AsyncTask<Void, Integer, String> {
            @Override
            protected void onPreExecute() {
                view.toggleUpdateProgress(true);
            }

            @Override
            protected void onPostExecute(String s) {
                view.openInstalledIntent(s);
            }

            @Override
            protected String doInBackground(Void... voids) {
                String link = String.format("%s%s", URLCollections.SERVER, URLCollections.GET_SERVER_APK);
                String path = "";
                try {
                    path = FileUtils.createTemporaryFile("sanwinvisitupdate", ".apk").getAbsolutePath();
                    URL url = new URL(link);
                    URLConnection connection = url.openConnection();
                    connection.connect();

                    int fileLength = connection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(path);
                    //OutputStream output = view.openFileOutputAsync("sanwinvisitupdate.apk");

                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //return "/data/data/com.sanken.sanwinvisit/files/sanwinvisitupdate.apk";
                return path;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                view.updateDownloadProgress(values);
            }
        }
        AsyncTaskDownloadApk asyncTaskDownloadApk = new AsyncTaskDownloadApk();
        return asyncTaskDownloadApk.execute();
    }


}
