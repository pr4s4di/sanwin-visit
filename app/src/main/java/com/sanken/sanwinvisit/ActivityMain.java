package com.sanken.sanwinvisit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.interfaces.IActivityMain;
import com.presenter.PresenterActivityMain;
import com.strings.ActionCollections;
import com.strings.SharedPreferenceCollections;
import com.utils.BitmapUtils;
import com.utils.FileUtils;
import com.utils.LayoutUtils;
import com.utils.PermissionsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ActivityMain extends AppCompatActivity implements IActivityMain.IView {

    private final String IMAGE_PREVIEW_STATE = "IMAGE_PREVIEW_STATE"; //buat visibility
    private final String IMAGE_PREVIEW_BITMAP = "IMAGE_PREVIEW_BITMAP"; //buat gambar di tempel lagi
    private final String IMAGE_REAL_BITMAP = "IMAGE_REAL_BITMAP";

    private final int REQUEST_PERMISSION_CAMERA = 1;
    private final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;
    private final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    private final int REQUEST_PERMISSION_READ_PHONE_STATE = 4;
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 5;
    private final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 6;
    private final int REQUEST_PERMISSION_ALL = 10;
    private final int REQUEST_CHECK_SETTINGS = 50;
    private final int REQUEST_HISTORY_KUNJUNGAN = 9;

    private final int RESULT_ACTIVITY_CAMERA = 200;

    private Button btnHistory;
    private Button btnSave;
    private Button btnCamera;
    private RadioButton rbMasuk;
    private RadioButton rbKeluar;
    private ImageView imageViewPreviewPhoto;
    private TextView txtViewStatusLocation;
    private TextInputEditText editTextKodeUser;
    private EditText editTextNamaUser;
    private RecyclerView recyclerViewList;
    private RadioGroup rbGroup;
    private ProgressBar progressBarLoading;
    private LinearLayout linearLayoutForm;
    private IActivityMain.IPresenter presenter;

    private Uri imageUri;
    private Bitmap bitmapCapturedImage;
    private File fileCapturedImage;
    private String phoneImei;
    private double latitude;
    private double longitude;

    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String currentPhotoPath;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ActionCollections.GPS_STATUS)) {
                checkGpsPermission();
            }
        }
    };


    private String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeGpsSettings();
        initializeComponent();
    }

    private void initializeGpsSettings() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*BitmapDrawable drawable = (BitmapDrawable) imageViewPreviewPhoto.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            outState.putInt(IMAGE_PREVIEW_STATE, imageViewPreviewPhoto.getVisibility());
            outState.putParcelable(IMAGE_PREVIEW_BITMAP, bitmap);
            outState.putParcelable(IMAGE_REAL_BITMAP, bitmapCapturedImage);
        }*/
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        int imageViewVisibility = savedInstanceState.getInt(IMAGE_PREVIEW_STATE);
//        Bitmap bitmap = savedInstanceState.getParcelable(IMAGE_PREVIEW_BITMAP);
//        Bitmap capturedImage = savedInstanceState.getParcelable(IMAGE_REAL_BITMAP);
//        if (bitmap != null) {
//            imageViewPreviewPhoto.setVisibility(imageViewVisibility);
//            imageViewPreviewPhoto.setImageBitmap(bitmap);
//            bitmapCapturedImage = capturedImage;
//        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (editTextKodeUser.isFocused()) {
                Rect outRect = new Rect();
                editTextKodeUser.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    editTextKodeUser.clearFocus();
                    LayoutUtils.hideKeyboard(this);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void toggleInput(boolean isDisable) {
        btnSave.setEnabled(isDisable);
        btnHistory.setEnabled(isDisable);
        btnCamera.setEnabled(isDisable);
        editTextNamaUser.setEnabled(isDisable);
        editTextKodeUser.setEnabled(isDisable);
    }

    private void initializeComponent() {
        btnHistory = findViewById(R.id.btnHistory);
        btnSave = findViewById(R.id.btnSave);
        btnCamera = findViewById(R.id.btnCamera);

        rbMasuk = findViewById(R.id.rbKunjungan);
        rbKeluar = findViewById(R.id.rbPhone);
        imageViewPreviewPhoto = findViewById(R.id.imageViewPreviewPhoto);
        txtViewStatusLocation = findViewById(R.id.txtViewStatusLocation);
        editTextKodeUser = findViewById(R.id.editTextKodeUser);
        editTextNamaUser = findViewById(R.id.editTextNamaUser);
        recyclerViewList = findViewById(R.id.recyclerViewListAbsen);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        rbGroup = findViewById(R.id.rbGroupHadir);
        linearLayoutForm = findViewById(R.id.linearLayoutForm);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        presenter = new PresenterActivityMain(this);
        txtViewStatusLocation.setText("OFF");

        btnHistory.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        editTextKodeUser.setOnKeyListener(this);
        editTextKodeUser.setOnFocusChangeListener(this);
        imageViewPreviewPhoto.setOnClickListener(this);
        progressBarLoading.setVisibility(View.GONE);
        rbMasuk.setOnClickListener(this);
        rbKeluar.setOnClickListener(this);

        editTextKodeUser.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        registerReceiver(receiver, new IntentFilter(ActionCollections.GPS_STATUS));
        setDataFromPhone();

        editTextKodeUser.setFocusable(false);
        checkAllPermissions();
    }

    private void setDataFromPhone() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceCollections.USER_DETAIL, MODE_PRIVATE);
        String kodeUser = sharedPreferences.getString(SharedPreferenceCollections.USERNAME, "");
        String namaUser = sharedPreferences.getString(SharedPreferenceCollections.NAME, "");
        editTextKodeUser.setText(kodeUser);
        editTextNamaUser.setText(namaUser);
        presenter.setKodeUser(kodeUser);
        presenter.setNamaUser(namaUser);
    }

    private void checkAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionsUtils.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_ALL);
            }
        } else {
            checkGpsPermission();
        }
    }

    private void checkGpsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
                return;
            }
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            builder.setAlwaysShow(true);
            Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
            task.addOnCompleteListener(this);
        }
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        txtViewStatusLocation.setText("ON");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /*private void checkGpsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
                //return;
            }
        }
        //checkPhoneStatePermission();
        txtViewStatusLocation.setText("ON");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            txtViewStatusLocation.setText("OFF");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
            result.addOnCompleteListener(this);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
        //}

    }*/

    public void onLocationChanged(Location location) {
        presenter.setLatitude(location.getLatitude());
        presenter.setLongitude(location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        getAddressFromLatLong();
        Log.d("latitude", String.valueOf(latitude));
        Log.d("longitude", String.valueOf(longitude));
    }

    private void getAddressFromLatLong() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            presenter.setAddress(addresses.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGpsPermission();
    }

    private void checkPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_READ_PHONE_STATE);
                return;
            }
        }
        checkReadWriteFilePermission();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneImei = telephonyManager.getImei();
        } else {
            phoneImei = telephonyManager.getDeviceId();
        }
        doAsyncTaskWork(EnumAsyncTask.DO_VALIDATE_IMEI);
        //Toast.makeText(this, phoneImei, Toast.LENGTH_SHORT).show();
    }

    private void checkReadWriteFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnCamera:
                checkCameraPermission();
                break;
            case R.id.btnSave:
                doValidateInput();
                break;
            case R.id.btnHistory:
                doValidateInputHistory();
                break;
            case R.id.rbPhone:
                presenter.setStatusUser("PHONE");
                break;
            case R.id.rbKunjungan:
                presenter.setStatusUser("KUNJUNGAN");
                break;
            case R.id.imageViewPreviewPhoto:
                goToActivityDetailPhoto();
                break;
        }
    }

    private void goToActivityDetailPhoto() {
        Intent intent = new Intent(this, ActivityDetailPhoto.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapCapturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("imagemode", "bytearray");
        intent.putExtra("imagepreview", byteArray);
        startActivity(intent);
    }

    private void doValidateInputHistory() {
        if (editTextKodeUser.getText().toString().trim().isEmpty() && editTextNamaUser.getText().toString().trim().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Gagal");
            builder.setMessage("Kode user dan nama user kosong, harap isi terlebih dahulu");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return;
        }
        goToActivityListKunjungan();
    }

    private void doValidateInput() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Harap nyalakan GPS terlebih dahulu");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
            return;
        }

        if (editTextKodeUser.getText().toString().trim().isEmpty()) {
            editTextKodeUser.setError("Harap isi kode anda");
            return;
        } else {
            editTextKodeUser.setError(null);
        }

        if (editTextNamaUser.getText().toString().trim().isEmpty()) {
            editTextNamaUser.setError("Harap isi nama anda");
            return;
        } else {
            editTextNamaUser.setError(null);
        }

        if (!rbMasuk.isChecked() && !rbKeluar.isChecked()) {
            rbMasuk.setError("Harap pilih salah satu");
            rbKeluar.setError("Harap pilih salah satu");
            return;
        } else {
            rbMasuk.setError(null);
            rbKeluar.setError(null);
        }

        if (imageViewPreviewPhoto.getDrawable() == null) {
            btnCamera.setError("Harap ambil foto terlebih dahulu");
            return;
        } else {
            btnCamera.setError(null);
        }


        doAsyncTaskWork(EnumAsyncTask.DO_SAVE_ABSEN);
    }

    private void goToActivityListKunjungan() {
        Intent intent = new Intent(this, ActivityListKunjungan.class);
        Bundle bundle = new Bundle();
        bundle.putString("kduser", editTextKodeUser.getText().toString());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_HISTORY_KUNJUNGAN);
    }

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                return;
            }
        }
        openCamera();
    }

    private void openCamera() {
        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePhoto.resolveActivity(getPackageManager()) != null) {
            //File photo;
            try {
                fileCapturedImage = FileUtils.createTemporaryFile("picture", ".jpg");
                currentPhotoPath = fileCapturedImage.getAbsolutePath();
            } catch (Exception e) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageUri = FileProvider.getUriForFile(this, "com.sanken.sanwinvisit.fileprovider", fileCapturedImage);
            } else {
                imageUri = Uri.fromFile(fileCapturedImage);
            }
            intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentTakePhoto, RESULT_ACTIVITY_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_PERMISSION_CAMERA:
                    openCamera();
                    break;
                case REQUEST_PERMISSION_READ_PHONE_STATE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkPhoneStatePermission();
                    }
                    break;
                case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkGpsPermission();
                    }
                    break;
                case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkReadWriteFilePermission();
                    }
                    break;
                case REQUEST_PERMISSION_ALL:
                    checkGpsPermission();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case RESULT_ACTIVITY_CAMERA:
                        setCapturedPicToImageView();
                        //setCapturedImageToImageView(data);
                        break;
                }
                switch (requestCode) {
                    case REQUEST_HISTORY_KUNJUNGAN:
                        break;
                }
                break;
        }
    }


    private void setCapturedPicToImageView() {
        try {
            imageViewPreviewPhoto.setVisibility(View.VISIBLE);
            int targetWidth = 300;
            int targetHeight = 300;
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapFactory.decodeFile(currentPhotoPath, options);
            options.inJustDecodeBounds = true;
            int scaleFactor = Math.min(options.outWidth / targetWidth, options.outHeight / targetHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            bitmapCapturedImage = BitmapFactory.decodeFile(currentPhotoPath, options);
            imageViewPreviewPhoto.setImageBitmap(bitmapCapturedImage);
            presenter.setImageByteArray(bitmapCapturedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCapturedImageToImageView(Intent data) {
        Matrix mat = new Matrix();
        if (fileCapturedImage != null) {
            if (!fileCapturedImage.getPath().isEmpty()) {
                try {
                    ExifInterface exif = new ExifInterface(fileCapturedImage.getPath());
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                    int rotateAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                        rotateAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                        rotateAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                        rotateAngle = 270;
                    mat.setRotate(rotateAngle, (float) imageViewPreviewPhoto.getWidth() / 2, (float) imageViewPreviewPhoto.getHeight() / 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        getContentResolver().notifyChange(imageUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
            //Ini Real Image
            int maxWidth = 1024, maxHeight = 1024;
            bitmap = BitmapUtils.resizeImage(maxWidth, maxHeight, bitmap, mat);
            bitmapCapturedImage = bitmap;
            //Ini buat preview
            maxWidth /= 2;
            maxHeight /= 2;
            bitmap = BitmapUtils.resizeImage(maxWidth, maxHeight, bitmap, new Matrix());
            imageViewPreviewPhoto.setImageBitmap(bitmap);
            imageViewPreviewPhoto.setVisibility(View.VISIBLE);
            presenter.setImageByteArray(bitmapCapturedImage);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
        try {
            LocationSettingsResponse response = task.getResult(ApiException.class);
        } catch (ApiException e) {
            switch (e.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(
                                this,
                                this.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ex) {
                        // Ignore the error.
                    } catch (ClassCastException ex) {
                        // Ignore, should be an impossible error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        }
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        linearLayoutForm.setEnabled(false);
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        linearLayoutForm.setEnabled(true);
        progressBarLoading.setVisibility(View.GONE);
        switch (enumAsyncTask) {
            case DO_FIND_USER_BY_KODE_USER:
                setRetrievedDataToEditText(result);
                break;
            case DO_VALIDATE_IMEI:
                break;
            case DO_SAVE_ABSEN:
                setRetrievedMessageFromSaveDataToDialog(result);
                break;
        }
    }

    private void setRetrievedMessageFromSaveDataToDialog(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            final boolean resultBoolean = jsonObject.getBoolean("result");
            String title = "";
            if (resultBoolean) {
                title = "Sukses";
            } else {
                title = "Error";
            }
            String message = jsonObject.getString("message");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (resultBoolean) {
                        resetInputtedData();
                    }
                    dialog.dismiss();

                }
            });
            builder.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Reset inputted data when input from user success
    private void resetInputtedData() {
/*        editTextKodeUser.setText("");
        editTextNamaUser.setText("");*/
        rbGroup.clearCheck();
        imageViewPreviewPhoto.setVisibility(View.GONE);
        bitmapCapturedImage = null;
        presenter.resetModelSaveAbsensi();
    }

    private void setRetrievedDataToEditText(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                boolean isFound = jsonObject.getBoolean("isfound");
                if (isFound) {
                    String kode = jsonObject.getString("kode");
                    String nama = jsonObject.getString("nama");
                    editTextKodeUser.setText(kode);
                    editTextNamaUser.setText(nama);
                    presenter.setKodeUser(kode);
                    presenter.setNamaUser(nama);

                } else {
                    String message = jsonObject.getString("message");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    editTextNamaUser.setText("");
                    editTextKodeUser.setText("");
                }
            } else {
                String message = jsonObject.getString("message");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                editTextNamaUser.setText("");
                editTextKodeUser.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case DO_VALIDATE_IMEI:
                break;
            case DO_SAVE_ABSEN:
                return presenter.doSaveAbsensi();
            case DO_FIND_USER_BY_KODE_USER:
                return presenter.doFindUserByKodeUser(editTextKodeUser.getText().toString().trim());
        }
        return null;
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.editTextKodeUser:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            //doAsyncTaskWork(EnumAsyncTask.DO_FIND_USER_BY_KODE_USER);
                            return true;
                    }
                }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.editTextKodeUser:
                if (!hasFocus) {
                    doAsyncTaskWork(EnumAsyncTask.DO_FIND_USER_BY_KODE_USER);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
