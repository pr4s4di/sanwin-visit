package com.sanken.sanwinvisit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.interfaces.IActivityDetailPhoto;
import com.presenter.PresenterActivityDetailPhoto;
import com.strings.URLCollections;
import com.utils.TouchImageView;

public class ActivityDetailPhoto extends AppCompatActivity implements IActivityDetailPhoto.IView {

    private ScaleGestureDetector scaleGestureDetector;
    private IActivityDetailPhoto.IPresenter presenter;
    private ImageView imageViewPreviewPhoto;
    private float scaleFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        initializeComponent();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void initializeComponent() {
        presenter = new PresenterActivityDetailPhoto(this);
        imageViewPreviewPhoto = findViewById(R.id.imageViewPreviewPhoto);
        TouchImageView touchImageViewPreviewPhoto = findViewById(R.id.touchImageViewPreviewPhoto);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        Intent intent = getIntent();
        String pathImage = intent.getStringExtra("imagepreview");
        String modeImage = intent.getStringExtra("imagemode");
        switch (modeImage) {
            case "bytearray":
                byte[] byteArray = getIntent().getByteArrayExtra("imagepreview");
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                touchImageViewPreviewPhoto.setImageBitmap(bmp);
                break;
            case "url":
                Glide.with(this).load(String.format("%s%s", URLCollections.SERVER, pathImage)).into(touchImageViewPreviewPhoto);
                break;
        }

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        //super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor = imageViewPreviewPhoto.getScaleX();
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            imageViewPreviewPhoto.setScaleX(scaleFactor);
            imageViewPreviewPhoto.setScaleY(scaleFactor);
            return true;
        }
    }
}
