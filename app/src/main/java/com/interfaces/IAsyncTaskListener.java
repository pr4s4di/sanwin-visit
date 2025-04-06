package com.interfaces;

import com.enums.EnumAsyncTask;

public interface IAsyncTaskListener {

    void onPreExecute(EnumAsyncTask enumAsyncTask);
    void onPostExecute(EnumAsyncTask enumAsyncTask, String result);
    String doInBackground(EnumAsyncTask enumAsyncTask);
    void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values);
}
