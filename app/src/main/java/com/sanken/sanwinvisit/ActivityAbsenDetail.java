package com.sanken.sanwinvisit;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.adapters.AdapterTabLayoutAbsenDetail;
import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IActivityAbsenDetail;
import com.presenter.PresenterActivityAbsenDetail;

public class ActivityAbsenDetail extends AppCompatActivity implements IActivityAbsenDetail.IView {

    private IActivityAbsenDetail.IPresenter presenter;
    private TabLayout tabLayoutAbsenDetail;
    private ViewPager viewPagerAbsenDetail;
    private boolean isWarningDialogShowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_detail);
        initializeComponent();
    }

    private void initializeComponent() {
        presenter = new PresenterActivityAbsenDetail(this);
        tabLayoutAbsenDetail = findViewById(R.id.tabLayoutAbsenDetail);
        viewPagerAbsenDetail = findViewById(R.id.viewPagerAbsenDetail);

        Bundle bundle = getIntent().getExtras();

        AdapterTabLayoutAbsenDetail adapterTabLayoutAbsenDetail = new AdapterTabLayoutAbsenDetail(getSupportFragmentManager());
        FragmentAbsenDetailNote fragmentAbsenDetailNote = new FragmentAbsenDetailNote();
        fragmentAbsenDetailNote.setArguments(bundle);
        adapterTabLayoutAbsenDetail.addFragment(fragmentAbsenDetailNote, "Note");

        FragmentAbsenDetailListItem fragmentAbsenDetailListItem = new FragmentAbsenDetailListItem();
        fragmentAbsenDetailListItem.setArguments(bundle);
        adapterTabLayoutAbsenDetail.addFragment(fragmentAbsenDetailListItem, "List Item");

        FragmentAbsenDetailPhoto fragmentAbsenDetailPhoto = new FragmentAbsenDetailPhoto();
        fragmentAbsenDetailPhoto.setArguments(bundle);
        adapterTabLayoutAbsenDetail.addFragment(fragmentAbsenDetailPhoto, "Foto");

        viewPagerAbsenDetail.setAdapter(adapterTabLayoutAbsenDetail);
        tabLayoutAbsenDetail.setupWithViewPager(viewPagerAbsenDetail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Absen Detail");
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_absen_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_absen_detail_save:
                doAsyncTaskWork(EnumAsyncTask.DO_SAVE_ABSEN_DETAIL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        /*if (!isWarningDialogShowed)
            showWarningAlertDialog();*/
    }

    private void showWarningAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Semua data yang diinput akan hilang, Apakah anda tetap yakin untuk kembali?");
        builder.setTitle("Warning");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isWarningDialogShowed = false;
            }
        });
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {

    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {

    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        return null;
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }
}
