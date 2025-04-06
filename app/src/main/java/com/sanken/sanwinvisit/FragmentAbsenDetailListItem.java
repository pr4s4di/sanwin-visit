package com.sanken.sanwinvisit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adapters.AdapterRecyclerViewListBarang;
import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IFragmentAbsenDetailListItem;
import com.models.ModelListBarang;
import com.presenter.PresenterFragmentAbsenDetailListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAbsenDetailListItem extends Fragment implements IFragmentAbsenDetailListItem.IView {

    private final int REQUEST_ACTIVITY_LIST_BARANG = 1;

    private String idKunjungan;
    private String kodeUser;
    private String kodeBarang;
    private ArrayList<ModelListBarang> barangs;
    private String selectedBarang;

    private IFragmentAbsenDetailListItem.IPresenter presenter;
    private Button btnSaveListBarang;
    private RecyclerView recyclerViewListBarang;
    private TextInputEditText editTextKodeBarang;
    private TextInputEditText editTextNamaBarang;
    private TextInputEditText editTextQtyBarang;
    private NestedScrollView nestedScrollView;

    public FragmentAbsenDetailListItem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_absen_detail_list_item, container, false);
        initializeComponent(view);
        return view;
    }

    private void initializeComponent(View view) {
        barangs = new ArrayList<>();
        presenter = new PresenterFragmentAbsenDetailListItem(this);
        idKunjungan = getArguments().getString("idkunjungan");
        kodeUser = getArguments().getString("kduser");
        btnSaveListBarang = view.findViewById(R.id.btnSaveListBarang);
        recyclerViewListBarang = view.findViewById(R.id.recyclerViewListBarang);
        editTextKodeBarang = view.findViewById(R.id.editTextKodeBarang);
        editTextNamaBarang = view.findViewById(R.id.editTextNamaBarang);
        editTextQtyBarang = view.findViewById(R.id.editTextQtyBarang);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        recyclerViewListBarang.setNestedScrollingEnabled(false);
        editTextKodeBarang.setOnClickListener(this);
        editTextNamaBarang.setOnClickListener(this);
        btnSaveListBarang.setOnClickListener(this);
        nestedScrollView.setFocusableInTouchMode(false);
        doAsyncTaskWork(EnumAsyncTask.GET_LIST_BARANG_ORDER);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editTextKodeBarang:
                goToListBarang();
                break;
            case R.id.editTextNamaBarang:
                goToListBarang();
                break;
            case R.id.btnSaveListBarang:
                doValidateInput();

                break;
        }
    }

    private void doValidateInput() {
        if (editTextKodeBarang.getText().toString().trim().isEmpty()) {
            editTextKodeBarang.requestFocus();
            editTextKodeBarang.setError("Harap masukan kode barang");
            return;
        } else {
            editTextKodeBarang.setError(null);
        }
        if (editTextNamaBarang.getText().toString().trim().isEmpty()) {
            editTextNamaBarang.requestFocus();
            editTextNamaBarang.setError("Harap masukan nama barang");
            return;
        } else {
            editTextNamaBarang.setError(null);
        }
        if (editTextQtyBarang.getText().toString().trim().isEmpty()) {
            editTextQtyBarang.requestFocus();
            editTextQtyBarang.setError("Harap masukan quantity barang");
            return;
        } else {
            editTextQtyBarang.setError(null);
        }

        doAsyncTaskWork(EnumAsyncTask.DO_SAVE_ABSEN_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_ACTIVITY_LIST_BARANG:
                        kodeBarang = data.getStringExtra("result");
                        doAsyncTaskWork(EnumAsyncTask.GET_LIST_BARANG);
                        break;
                }
                break;
        }
    }

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
    }

    private void goToListBarang() {
        Intent intent = new Intent(getActivity(), ActivityListBarang.class);
        startActivityForResult(intent, REQUEST_ACTIVITY_LIST_BARANG);
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {

    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        switch (enumAsyncTask) {
            case GET_LIST_BARANG:
                setDataToEditText(result);
                break;
            case DO_SAVE_ABSEN_DETAIL:
                showDialogResult(result);
                break;
            case GET_LIST_BARANG_ORDER:
                setDataToRecyclerView(result);
                break;
            case DO_DELETE_SELECTED_BARANG:
                showDialogResult(result);
                break;
        }
    }

    private void setDataToRecyclerView(String result) {
        try {
            barangs.clear();
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelListBarang barang = new ModelListBarang();
                        barang.setKodeBarang(jsonArray.getJSONObject(i).getString("kditem"));
                        barang.setNamaBarang(jsonArray.getJSONObject(i).getString("nmitem"));
                        barang.setQtyBarang(jsonArray.getJSONObject(i).getString("qty"));
                        barangs.add(barang);
                    }
                    AdapterRecyclerViewListBarang adapterRecyclerViewListBarang = new AdapterRecyclerViewListBarang(barangs, this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerViewListBarang.setLayoutManager(layoutManager);
                    recyclerViewListBarang.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewListBarang.setAdapter(adapterRecyclerViewListBarang);
                    adapterRecyclerViewListBarang.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(recyclerViewListBarang.getAdapter() != null){
            recyclerViewListBarang.getAdapter().notifyDataSetChanged();
        }
    }


    private void showDialogResult(String result) {
        try {
            String title = "";
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            String message = jsonObject.getString("message");
            if (resultBoolean) {
                title = "Sukses";
            } else {
                title = "Gagal";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            doAsyncTaskWork(EnumAsyncTask.GET_LIST_BARANG_ORDER);
            resetInputtedData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String id) {
        selectedBarang = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage(String.format("Apakah anda yakin untuk menghapus barang dengan kode %s?", id));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAsyncTaskWork(EnumAsyncTask.DO_DELETE_SELECTED_BARANG);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedBarang = null;
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void resetInputtedData() {
        editTextQtyBarang.setText("");
        editTextNamaBarang.setText("");
        editTextKodeBarang.setText("");
    }

    private void setDataToEditText(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                if (jsonArray.length() > 0) {
                    editTextKodeBarang.setText(jsonArray.getJSONObject(0).getString("kditem"));
                    editTextNamaBarang.setText(jsonArray.getJSONObject(0).getString("nmitem"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case GET_LIST_BARANG:
                return presenter.doGetBarangBySelectedKode(kodeBarang);
            case DO_SAVE_ABSEN_DETAIL:
                String kodeBarang = editTextKodeBarang.getText().toString();
                String namaBarang = editTextNamaBarang.getText().toString();
                String qty = editTextQtyBarang.getText().toString();
                return presenter.doSaveListBarang(idKunjungan, kodeBarang, namaBarang, qty, kodeUser);
            case GET_LIST_BARANG_ORDER:
                return presenter.doGetAllBarangByIdKunjungan(idKunjungan);
            case DO_DELETE_SELECTED_BARANG:
                return presenter.doDeleteBarangByIdBarang(selectedBarang, idKunjungan);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public void onViewHolderClick(View view, String id) {
        int idContent = view.getId();
        switch (idContent) {
            case R.id.imageViewDeleteItem:
                showAlertDialog(id);
                break;
        }
    }
}
