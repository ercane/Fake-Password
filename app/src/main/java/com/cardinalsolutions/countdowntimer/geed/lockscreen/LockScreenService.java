package com.cardinalsolutions.countdowntimer.geed.lockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesKeys;
import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesService;


/**
 * Lock Screen Service
 *
 * @author Andy
 */
public class LockScreenService extends Service{
    private static final String TAG = "SERVICE";
    public static boolean isRunning;
    private static SharedPreferencesService preferencesService;
    private static Context context;
    private BroadcastReceiver mReceiver;

    public static Context getContext(){
        return context;
    }

    public static SharedPreferencesService getPreferencesService(){
        return preferencesService;
    }

    public static void setPreferencesService(SharedPreferencesService preferencesService){
        if (LockScreenService.preferencesService == null) {
            LockScreenService.preferencesService = preferencesService;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);
        context = getApplicationContext();
        if (preferencesService == null) {
            preferencesService = new SharedPreferencesService(getSharedPreferences(SharedPreferencesKeys.ROOT,
                    MODE_PRIVATE));
        }
        isRunning = true;
        //startForeground();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "OnStartCommand");
        //super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
