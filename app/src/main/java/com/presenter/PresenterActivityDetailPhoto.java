package com.presenter;

import android.view.ScaleGestureDetector;

import com.interfaces.IActivityDetailPhoto;

public class PresenterActivityDetailPhoto implements IActivityDetailPhoto.IPresenter {

    private IActivityDetailPhoto.IView view;

    private float scaleFactor;

    public PresenterActivityDetailPhoto(IActivityDetailPhoto.IView view) {
        this.view = view;
    }

    @Override
    public boolean onScaleImage(float scaleFactor, ScaleGestureDetector detector) {
        return true;
    }
}
