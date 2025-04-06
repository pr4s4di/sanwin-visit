package com.sanken.sanwinvisit;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.adapters.AdapterRecyclerViewListAbsen;
import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IActivityListKunjungan;
import com.models.ModelListAbsen;
import com.presenter.PresenterActivityListKunjungan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListKunjungan extends AppCompatActivity implements IActivityListKunjungan.IView {

    private IActivityListKunjungan.IPresenter presenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewListAbsen;
    private ArrayList<ModelListAbsen> absens;
    private ConstraintLayout constraintLayoutNotFound;
    private ConstraintLayout constraintLayoutLoading;

    private String kodeUser;
    private String titleToolbar = "List History Absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kunjungan);
        initializeComponent();

    }

    private void initializeComponent() {
        absens = new ArrayList<>();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewListAbsen = findViewById(R.id.recyclerViewListAbsen);
        constraintLayoutNotFound = findViewById(R.id.constraintLayoutNotFound);
        constraintLayoutLoading = findViewById(R.id.constraintLayoutLoading);
        presenter = new PresenterActivityListKunjungan(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle extras = getIntent().getExtras();
        kodeUser = extras.getString("kduser");
        doStartBackgroundTask(EnumAsyncTask.GET_ABSEN_DATA);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titleToolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void doStartBackgroundTask(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
    }

    @Override
    public void onRefresh() {
        doStartBackgroundTask(EnumAsyncTask.GET_ABSEN_DATA);
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        constraintLayoutLoading.setVisibility(View.VISIBLE);
        constraintLayoutNotFound.setVisibility(View.GONE);
        recyclerViewListAbsen.setVisibility(View.GONE);
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                absens.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelListAbsen absen = new ModelListAbsen();
                    absen.setKodeKunjungan(jsonArray.getJSONObject(i).getString("kdkunjungan"));
                    absen.setStatus(jsonArray.getJSONObject(i).getString("stat"));
                    absen.setWaktuHadir(String.format("Check In : %s %s", jsonArray.getJSONObject(i).getString("tgl"), jsonArray.getJSONObject(i).getString("jam")));
                    absen.setAlamat("Toko : " + jsonArray.getJSONObject(i).getString("note1"));
                    absen.setWaktuCheckOut("Check Out : " + jsonArray.getJSONObject(i).getString("tgl_checkout").toString());
                    absens.add(absen);
                }
                AdapterRecyclerViewListAbsen adapter = new AdapterRecyclerViewListAbsen(absens, this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewListAbsen.setLayoutManager(layoutManager);
                recyclerViewListAbsen.setItemAnimator(new DefaultItemAnimator());
                recyclerViewListAbsen.setAdapter(adapter);
                recyclerViewListAbsen.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutNotFound.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
        constraintLayoutLoading.setVisibility(View.GONE);
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        return presenter.getDataByKodeUser(kodeUser);
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public void onViewHolderClick(View view, String id) {
        int idContent = view.getId();
        switch (idContent) {
            case R.id.cardViewListAbsen:
                goToListKunjunganDetail(id);
                break;
        }
    }

    private void goToListKunjunganDetail(String id) {
        Intent intent = new Intent(this, ActivityAbsenDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString("idkunjungan", id);
        bundle.putString("kduser", kodeUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
