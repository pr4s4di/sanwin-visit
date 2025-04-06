package com.interfaces;

import android.view.ScaleGestureDetector;

public interface IActivityDetailPhoto {

    interface IPresenter
    {

        boolean onScaleImage(float scaleFactor, ScaleGestureDetector detector);
    }

    interface IView
    {

    }
}
