package com.capcorp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.capcorp.ui.ProviderLocationActivity;

import java.util.Objects;

import cafe.adriel.kbus.KBus;

public class GpsLocationReceiver extends BroadcastReceiver {

    public static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

            if (!checkConnection(context)) {


                if (firstConnect) {

                    // do subroutines here
                    Intent pushIntent = new Intent(context, ProviderLocationActivity.class);
                    pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(pushIntent);
                    firstConnect = false;
                }


            } else {


                if (firstConnect) {

                    if (Objects.requireNonNull(MyApplication.Companion.getInstnace()).isActivityVisible()) {

                        KBus.INSTANCE.post(new LocationEvent("OFF"));

                    }

                } else {

                    firstConnect = true;


                }

            }
        }


    }

    private boolean checkConnection(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
}