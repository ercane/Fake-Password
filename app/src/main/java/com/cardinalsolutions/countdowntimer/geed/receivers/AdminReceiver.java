package com.cardinalsolutions.countdowntimer.geed.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class AdminReceiver extends DeviceAdminReceiver{
    void showToast(Context context, CharSequence msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent){

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent){
        return "Are you sure?";
    }

    @Override
    public void onDisabled(Context context, Intent intent){

    }

    @Override
    public void onPasswordChanged(Context context, Intent intent){
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent){
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent){
    }
}
