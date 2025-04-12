package com.sanken.sanwinvisit;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
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
    private final int pageSize = 10; // Or whatever page size you're using

    private String kodeUser;
    private ProgressBar loadMoreProgressBar;
    // Pagination variables
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLoadMore = false;
    private boolean isLastPage = false;
    private LinearLayoutManager layoutManager;
    private AdapterRecyclerViewListAbsen adapter;

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
        loadMoreProgressBar = findViewById(R.id.loadMoreProgressBar);
        presenter = new PresenterActivityListKunjungan(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle extras = getIntent().getExtras();
        kodeUser = extras.getString("kduser");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String titleToolbar = "List History Absensi";
        getSupportActionBar().setTitle(titleToolbar);

        initPagination();
        loadFirstPage(); // Load the initial data
    }

    private void initPagination() {
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListAbsen.setLayoutManager(layoutManager);
        recyclerViewListAbsen.setItemAnimator(new DefaultItemAnimator());
        adapter = new AdapterRecyclerViewListAbsen(absens, this); // Initialize adapter here
        recyclerViewListAbsen.setAdapter(adapter);

        recyclerViewListAbsen.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void loadFirstPage() {
        currentPage = 1;
        isLastPage = false;
        loadData();
    }

    private void loadNextPage() {
        isLoadMore = true;
        currentPage++;
        loadData();
    }

    private void loadData() {
        doStartBackgroundTask(EnumAsyncTask.GET_ABSEN_DATA);
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
        loadFirstPage();
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        if (isLoading) {
            constraintLayoutLoading.setVisibility(View.VISIBLE);
            constraintLayoutNotFound.setVisibility(View.GONE);
            recyclerViewListAbsen.setVisibility(View.GONE);
        }

        if (isLoadMore) {
            loadMoreProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                JSONObject messageObject = jsonObject.getJSONObject("message");
                JSONArray dataArray = messageObject.getJSONArray("data");
                int totalRecords = messageObject.getInt("total_records");

                if (currentPage == 1) {
                    absens.clear(); // Clear the list for the first page load
                }

                ArrayList<ModelListAbsen> newAbsens = new ArrayList<>();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject kunjunganObject = dataArray.getJSONObject(i);
                    ModelListAbsen absen = new ModelListAbsen();
                    absen.setKodeKunjungan(kunjunganObject.getString("kdkunjungan"));
                    absen.setStatus(kunjunganObject.getString("stat"));
                    absen.setWaktuHadir(String.format("Check In : %s %s", kunjunganObject.getString("tgl"), kunjunganObject.getString("jam")));
                    absen.setAlamat("Toko : " + kunjunganObject.getString("note1"));
                    String tglCheckout = kunjunganObject.getString("tgl_checkout");
                    if (!tglCheckout.equals("null")) {
                        absen.setWaktuCheckOut("Check Out : " + tglCheckout);
                    } else {
                        absen.setWaktuCheckOut("Check Out : -");
                    }
                    newAbsens.add(absen);
                }

                absens.addAll(newAbsens); // Add new items to the list
                if (currentPage == 1) {
                    adapter.notifyDataSetChanged(); // Notify for the first load
                    recyclerViewListAbsen.setVisibility(View.VISIBLE);
                } else {
                    adapter.notifyDataSetChanged(); // Notify the adapter about the new items
                }

                // Calculate total pages
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                isLastPage = currentPage >= totalPages;
            } else {
                constraintLayoutNotFound.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        } finally {
            isLoading = false;
            isLoadMore = false;
            swipeRefreshLayout.setRefreshing(false);
            constraintLayoutLoading.setVisibility(View.GONE);
            loadMoreProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        return presenter.getDataByKodeUser(kodeUser, currentPage);
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {
    }

    @Override
    public void onViewHolderClick(View view, String id) {
        int idContent = view.getId();
        if (idContent == R.id.cardViewListAbsen) {
            goToListKunjunganDetail(id);
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