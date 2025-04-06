package com.asynctasks;

import android.os.AsyncTask;

import com.enums.EnumAsyncTask;
import com.interfaces.IAsyncTaskListener;

public class AsyncTaskBackground extends AsyncTask<Void, Void, String> {

    private IAsyncTaskListener listener;
    private EnumAsyncTask enumAsyncTask;

    @Override
    protected void onPreExecute() {
        if(listener != null)
        {
            listener.onPreExecute(enumAsyncTask);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(listener != null)
        {
            listener.onPostExecute(enumAsyncTask, s);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if(listener != null)
        {
            listener.onProgressUpdate(enumAsyncTask, values);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (listener != null) {
            return listener.doInBackground(enumAsyncTask);
        }
        return null;
    }

    public void setListener(IAsyncTaskListener listener) {
        this.listener = listener;
    }

    public void setEnumAsyncTask(EnumAsyncTask enumAsyncTask) {
        this.enumAsyncTask = enumAsyncTask;
    }
}
