package com.capcorp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import androidx.constraintlayout.widget.ConstraintLayout;

public class SlidingConstraintLayout extends ConstraintLayout {
    private final float xFraction = 0;

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    public SlidingConstraintLayout(Context context) {
        super(context);
    }

    public SlidingConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getXFraction() {
        final int width = getWidth();
        if (width != 0) return getX() / getWidth();
        else return getX();
    }

    public void setXFraction(float xFraction) {
        final int width = getWidth();
        float newWidth = (width > 0) ? (xFraction * width) : -9999;
        setX(newWidth);
    }
}
