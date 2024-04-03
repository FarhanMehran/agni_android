package com.capcorp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

@CoordinatorLayout.DefaultBehavior(ButtonLayout.Behavior.class)
public class ButtonLayout extends LinearLayout {


    public ButtonLayout(Context context) {
        super(context);
    }

    public ButtonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static class Behavior extends
            CoordinatorLayout.Behavior<ButtonLayout> {

        public boolean layoutDependsOn(CoordinatorLayout parent, ButtonLayout child, View dependency) {
            return dependency instanceof Snackbar.SnackbarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, ButtonLayout child, View dependency) {
            if (dependency instanceof Snackbar.SnackbarLayout) {
                this.updateFabTranslationForSnackbar(parent, child, dependency);
            }
            return false;
        }

        private void updateFabTranslationForSnackbar(CoordinatorLayout parent, ButtonLayout fab, View snackbar) {
            //copy from FloatingActionButton.Behavior
        }

    }

}