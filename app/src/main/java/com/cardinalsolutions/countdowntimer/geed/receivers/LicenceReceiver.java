package com.cardinalsolutions.countdowntimer.geed.receivers;

import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cardinalsolutions.countdowntimer.geed.act.MainActivity;
import com.cardinalsolutions.countdowntimer.geed.knox.KnoxUtils;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;


public class LicenceReceiver extends BroadcastReceiver{
    void showToast(Context context, CharSequence msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action.equals(EnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
            String result = intent.getStringExtra(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
            //showToast(context, "License activation: " + result);
            if (result.toLowerCase().contains("success")) {
                LockScreenService.getPreferencesService().setIsKnoxEnabled(true);
                if (MainActivity.getAdminEnabledHandler() != null) {
                    MainActivity.getAdminEnabledHandler().sendEmptyMessage(25);
                }
                KnoxUtils.setAdminRemovable(false);
            }
        }
    }
}