package com.sanken.sanwinvisit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.adapters.AdapterRecyclerViewListBarang;
import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IActivityListBarang;
import com.models.ModelListBarang;
import com.presenter.PresenterActivityListBarang;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListBarang extends AppCompatActivity implements IActivityListBarang.IView {

    private IActivityListBarang.IPresenter presenter;
    private RecyclerView recyclerViewListBarang;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout constraintLayoutNotFound;
    private ConstraintLayout constraintLayoutLoading;

    private ArrayList<ModelListBarang> barangs;
    private AdapterRecyclerViewListBarang adapterRecyclerViewListBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang);
        initializeComponent();
    }

    private void initializeComponent() {
        barangs = new ArrayList<>();
        presenter = new PresenterActivityListBarang(this);
        recyclerViewListBarang = findViewById(R.id.recyclerViewListBarang);
        constraintLayoutNotFound = findViewById(R.id.constraintLayoutNotFound);
        constraintLayoutLoading = findViewById(R.id.constraintLayoutLoading);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        doAsyncTaskWork(EnumAsyncTask.GET_LIST_BARANG);

        getSupportActionBar().setTitle("List Barang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
    }

    @Override
    public void onRefresh() {
        doAsyncTaskWork(EnumAsyncTask.GET_LIST_BARANG);
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        constraintLayoutLoading.setVisibility(View.VISIBLE);
        constraintLayoutNotFound.setVisibility(View.GONE);
        recyclerViewListBarang.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_barang_detail, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        setRetrievedListBarangToRecyclerView(result);
    }

    private void setRetrievedListBarangToRecyclerView(String result) {
        swipeRefreshLayout.setRefreshing(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");

            if (resultBoolean) {
                barangs.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelListBarang barang = new ModelListBarang();
                    barang.setKodeBarang(jsonArray.getJSONObject(i).getString("kditem"));
                    barang.setNamaBarang(jsonArray.getJSONObject(i).getString("nmitem"));
                    barang.setQtyBarang("");
                    barangs.add(barang);
                }

                adapterRecyclerViewListBarang = new AdapterRecyclerViewListBarang(barangs, this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewListBarang.setLayoutManager(layoutManager);
                recyclerViewListBarang.setItemAnimator(new DefaultItemAnimator());
                recyclerViewListBarang.setAdapter(adapterRecyclerViewListBarang);
                recyclerViewListBarang.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutNotFound.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        constraintLayoutLoading.setVisibility(View.GONE);
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        return presenter.getListBarang();
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public void onViewHolderClick(View view, String id) {
        int idContent = view.getId();
        if (idContent == R.id.cardViewListBarang) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", id);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterRecyclerViewListBarang.filter(newText);
        return true;
    }
}
