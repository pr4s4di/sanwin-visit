package com.sanken.sanwinvisit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asynctasks.AsyncTaskBackground;
import com.bumptech.glide.Glide;
import com.enums.EnumAsyncTask;
import com.interfaces.IFragmentAbsenDetailPhoto;
import com.presenter.PresenterFragmentAbsenDetailPhoto;
import com.strings.URLCollections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAbsenDetailPhoto extends Fragment implements IFragmentAbsenDetailPhoto.IView {

    private IFragmentAbsenDetailPhoto.IPresenter presenter;

    private String idKunjungan;
    private String pathImages;
    private ImageView imageViewFotoKunjungan;
    private TextView txtViewStatus;
    private TextView txtViewAlamat;
    private TextView txtViewDateTime;
    private TextView txtViewTglCheckOut;
    private ImageView imageViewStatus;
    private Button btnCheckOut;
    private ProgressBar progressBarLoading;

    public FragmentAbsenDetailPhoto() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_absen_detail_photo, container, false);
        initializeComponent(view);
        return view;
    }

    private void initializeComponent(View view) {
        presenter = new PresenterFragmentAbsenDetailPhoto(this);
        idKunjungan = getArguments().getString("idkunjungan");
        imageViewFotoKunjungan = view.findViewById(R.id.imageViewFotoKunjungan);
        txtViewStatus = view.findViewById(R.id.txtViewStatus);
        txtViewAlamat = view.findViewById(R.id.txtViewAlamat);
        txtViewDateTime = view.findViewById(R.id.txtViewDateTime);
        txtViewTglCheckOut = view.findViewById(R.id.txtViewTglCheckOut);
        imageViewStatus = view.findViewById(R.id.imageViewStatus);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);

        doAsyncTaskWork(EnumAsyncTask.GET_ABSEN_DATA);
        imageViewFotoKunjungan.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);
    }

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.execute();
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case GET_ABSEN_DATA:
                break;
            case DO_CHECKOUT:
                toggleProgressDialog(true);
                break;
        }
    }

    private void toggleProgressDialog(boolean v) {
        if (v == true) {
            btnCheckOut.setVisibility(View.GONE);
            progressBarLoading.setVisibility(View.VISIBLE);
        } else {
            btnCheckOut.setVisibility(View.VISIBLE);
            progressBarLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        switch (enumAsyncTask) {
            case GET_ABSEN_DATA:
                setImageDataToImageView(result);
                break;
            case DO_CHECKOUT:
                toggleProgressDialog(false);
                showDialogResult(result);
                break;
        }
    }

    private void showDialogResult(String result) {
        JSONObject jsonObject = null;
        String message = "";
        String title = "Gagal";
        try {
            jsonObject = new JSONObject(result);
            boolean result1 = jsonObject.getBoolean("result");
            if (result1) {
                title = "Sukses";
                btnCheckOut.setVisibility(View.GONE);
            }
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            message = e.getMessage();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void setImageDataToImageView(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                JSONObject messageJsonObject = jsonObject.getJSONObject("message");
                JSONArray jsonArray = messageJsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    String pathImage = jsonArray.getJSONObject(0).getString("nmfoto");
                    String status = jsonArray.getJSONObject(0).getString("stat");
                    String date = jsonArray.getJSONObject(0).getString("tgl");
                    String jam = jsonArray.getJSONObject(0).getString("jam");
                    String alamat = jsonArray.getJSONObject(0).getString("alamat");
                    String checkout = jsonArray.getJSONObject(0).getString("tgl_checkout");
                    if(checkout != "null"){
                        btnCheckOut.setVisibility(View.GONE);
                    }

                    txtViewStatus.setText(status);
                    txtViewDateTime.setText(String.format("Check In : \n%s %s", date, jam));
                    txtViewTglCheckOut.setText(String.format("Check Out : \n%s", checkout));
                    txtViewAlamat.setText(alamat);
                    switch (status) {
                        case "MASUK":
                            imageViewStatus.setImageResource(R.mipmap.ic_login);
                            break;
                        case "KELUAR":
                            imageViewStatus.setImageResource(R.mipmap.ic_logout);
                            break;
                        case "KUNJUNGAN":
                            imageViewStatus.setImageResource(R.drawable.ic_car_orange);
                            break;
                        case "PHONE":
                            imageViewStatus.setImageResource(R.drawable.ic_phone_blue);
                            break;
                    }
                    pathImages = pathImage;
                    Glide.with(this).load(String.format("%s%s", URLCollections.SERVER, pathImage)).into(imageViewFotoKunjungan);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case GET_ABSEN_DATA:
                return presenter.doGetPhotoData(idKunjungan);
            case DO_CHECKOUT:
                return presenter.doCheckOut(idKunjungan);
            default:
                return "";
        }
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageViewFotoKunjungan) {
            Intent intent = new Intent(getActivity(), ActivityDetailPhoto.class);
            intent.putExtra("imagepreview", pathImages);
            intent.putExtra("imagemode", "url");
            startActivity(intent);
        } else if (id == R.id.btnCheckOut) {
            openDialogConfirmation();
        }
    }

    private void openDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Info");
        builder.setMessage("Apakah anda yakin ingin melakukan checkout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                doAsyncTaskWork(EnumAsyncTask.DO_CHECKOUT);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
