package com.sanken.sanwinvisit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IActivitySplashScreen;
import com.presenter.PresenterActivitySplashScreen;
import com.strings.SharedPreferenceCollections;
import com.utils.FileUtils;
import com.utils.PermissionsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ActivitySplashScreen extends AppCompatActivity implements IActivitySplashScreen.IView {

    private final int REQUEST_PERMISSION_ALL = 10;
    private AsyncTask downloadTask;

    private IActivitySplashScreen.IPresenter presenter;
    private final int SPLASH_SCREEN_TIME_OUT = 3000;
    private TextView textViewSanwinVisit;
    private View viewLinesFirst;
    private View viewLinesSecond;
    private View viewLinesThird;
    private ImageView imageViewLogoSv;
    private Button btnLogin;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;

    private LinearLayout linearLayoutCheckUpdates;
    private LinearLayout linearLayoutLoginForm;
    private ProgressBar progressBarCheckDownload;
    private TextView textViewCheckStatus;

    private String versionName;
    private final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initializeComponent();

    }

    private void initializeComponent() {
        // TODO prefer use build config
        // versionName = BuildConfig.VERSION_NAME;
        versionName = "1.0.1";
        presenter = new PresenterActivitySplashScreen(this);
        progressBarCheckDownload = findViewById(R.id.progressBarDownloadApk);
        textViewCheckStatus = findViewById(R.id.txtViewCheckingUpdates);
        textViewSanwinVisit = findViewById(R.id.txtViewSanwinVisit);
        linearLayoutCheckUpdates = findViewById(R.id.linearLayoutCheckUpdates);
        linearLayoutLoginForm = findViewById(R.id.linearLayoutLoginForm);
        viewLinesFirst = findViewById(R.id.viewlinesFirst);
        viewLinesSecond = findViewById(R.id.viewlinesSecond);
        viewLinesThird = findViewById(R.id.viewlinesThird);
        imageViewLogoSv = findViewById(R.id.imageViewLogoSv);
        btnLogin = findViewById(R.id.btnLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        linearLayoutCheckUpdates.setVisibility(View.GONE);
        editTextUsername.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btnLogin.setOnClickListener(this);
        linearLayoutLoginForm.setAlpha(0);
        linearLayoutLoginForm.setVisibility(View.GONE);

        startAnimation(imageViewLogoSv, R.anim.anim_slide_down, 0);
        startAnimation(viewLinesThird, R.anim.anim_slide_down, 250);
        startAnimation(viewLinesSecond, R.anim.anim_slide_down, 500);
        startAnimation(viewLinesFirst, R.anim.anim_slide_down, 750);
        startAnimation(textViewSanwinVisit, R.anim.anim_slide_up, 750);
        startAnimation(linearLayoutCheckUpdates, R.anim.anim_slide_up, 750);

        getSupportActionBar().hide();
        checkAllPermissions();
        //startCheckingUpdates();
        //startSplashScreen();
    }

    private void checkAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionsUtils.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_ALL);
                return;
            }
        }

        doAsyncTaskWork(EnumAsyncTask.GET_SERVER_VERSION);
    }

    private void checkIfUpdatesIsExists() {
        try {
            File file = new File(FileUtils.getTempLocation(getApplicationContext(), "sanwinvisitupdate/", ".apk"));
            if (file != null) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_PERMISSION_ALL:
                    Log.d("grantresult", String.valueOf(grantResults.length));
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Harap terima semua permissions supaya program dapat dipakai", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    doAsyncTaskWork(EnumAsyncTask.GET_SERVER_VERSION);
                    break;
            }
        }
    }

    private void startAnimation(View view, int animLayout, int delay) {
        Animation animation = AnimationUtils.loadAnimation(this, animLayout);
        animation.setStartOffset(delay);
        view.startAnimation(animation);
    }

    private void startSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceCollections.USER_DETAIL, Context.MODE_PRIVATE);
                String username = sharedPreferences.getString(SharedPreferenceCollections.USERNAME, "");
                if (!username.trim().isEmpty()) {
                    goToMainActivity();
                } else {
                    linearLayoutLoginForm.setVisibility(View.VISIBLE);
                    linearLayoutLoginForm.setAlpha(1);
                    startAnimation(linearLayoutLoginForm, R.anim.anim_login_form, 0);
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case GET_SERVER_VERSION:
                textViewCheckStatus.setVisibility(View.VISIBLE);
                progressBarCheckDownload.setVisibility(View.VISIBLE);
                break;
            case DO_LOGIN:
                toggleLoginForm(false);
                toggleUpdateForm(View.VISIBLE);
                progressBarCheckDownload.setIndeterminate(true);
                textViewCheckStatus.setText("Please wait");
                break;
        }
    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        switch (enumAsyncTask) {
            case DO_LOGIN:
                toggleLoginForm(true);
                toggleUpdateForm(View.GONE);
                progressBarCheckDownload.setIndeterminate(false);
                textViewCheckStatus.setText("");
                showLoginResult(result);
                break;
            case GET_SERVER_VERSION:
                compareServerVerWithCurrentVer(result);
                break;
        }
    }

    private void compareServerVerWithCurrentVer(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                String serverVersion = jsonObject.getString("current_version");
                if (!versionName.equals(serverVersion)) {
                    progressBarCheckDownload.setIndeterminate(false);
                    downloadTask = presenter.getServerApk();
                } else {
                    startSplashScreen();
                }
            } else {
                //Kalo kejadian misal gaada internet, nah dia tampilin ini. Next time sih keanya mau munculin button refresh atau close dan bukan program lagi.
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLoginResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                boolean isFound = jsonObject.getBoolean("isfound");
                if (isFound) {
                    //Save data and
                    String username = jsonObject.getString("kode");
                    String name = jsonObject.getString("nama");
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceCollections.USER_DETAIL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SharedPreferenceCollections.USERNAME, username);
                    editor.putString(SharedPreferenceCollections.NAME, name);
                    editor.commit();
                    goToMainActivity();
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }


    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        switch (enumAsyncTask) {
            case DO_LOGIN:
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                return presenter.doLogin(username, password);
            case GET_SERVER_VERSION:

                return presenter.getServerVersion();
        }
        return null;
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) {
            doAsyncTaskWork(EnumAsyncTask.DO_LOGIN);
        }
    }

    private AsyncTaskBackground doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setListener(ActivitySplashScreen.this);
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.execute();
        return asyncTaskBackground;
    }

    @Override
    public void toggleUpdateProgress(boolean b) {
        if (b) {
            toggleUpdateForm(View.VISIBLE);
            //linearLayoutCheckUpdates.setVisibility(View.VISIBLE);
        } else {
            toggleUpdateForm(View.GONE);
            //linearLayoutCheckUpdates.setVisibility(View.GONE);
        }
    }

    private void toggleLoginForm(boolean toggle) {
        btnLogin.setEnabled(toggle);
        editTextUsername.setEnabled(toggle);
        editTextPassword.setEnabled(toggle);
    }

    private void toggleUpdateForm(int viewStatus) {
        linearLayoutCheckUpdates.setVisibility(viewStatus);
        textViewCheckStatus.setVisibility(viewStatus);
        progressBarCheckDownload.setVisibility(viewStatus);

    }

    @Override
    public void updateDownloadProgress(Integer[] values) {
        textViewCheckStatus.setText(String.format("Downloading updates : %s%%", values[0]));
        progressBarCheckDownload.setProgress((int) values[0]);
    }

    @Override
    public void openInstalledIntent(String s) {
        Uri apkUri = null;
        File apkFile = null;
        Intent i = new Intent();
        i.setAction(Intent.ACTION_INSTALL_PACKAGE);

        try {
            apkFile = new File(s);
        } catch (Exception e) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(this, "com.sanken.sanwinvisit.fileprovider", apkFile);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d("Lofting", "About to install new .apk");
        startActivity(i);
    }

    @Override
    public OutputStream openFileOutputAsync(String path) {
        try {
            return openFileOutput(path, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
/*        super.onBackPressed();
        if (downloadTask != null) {
            downloadTask.cancel(true);
            downloadTask = null;
        }
        finish();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkIfUpdatesIsExists();
    }
}
