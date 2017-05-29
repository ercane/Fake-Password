package com.cardinalsolutions.countdowntimer.geed.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.cardinalsolutions.countdowntimer.geed.knox.KnoxUtils;
import com.cardinalsolutions.countdowntimer.geed.util.Constant;


/**
 * Lock Screen Receiver
 *
 * @author Andy
 */
public class LockScreenReceiver extends BroadcastReceiver{

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if (check()) {
                Intent lockIntent = new Intent(Constant.LOCK_SCREEN_ACTION);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                context.startActivity(lockIntent);
                KnoxUtils.setHomeButtonState(false);
            } else {
                if (!LockScreenService.isRunning) {
                    context.startService(new Intent(context, LockScreenService.class));
                }
            }

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // do other things if you need
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (check()) {
                Intent lockIntent = new Intent(Constant.LOCK_SCREEN_ACTION);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                context.startActivity(lockIntent);
            } else {
                if (!LockScreenService.isRunning) {
                    context.startService(new Intent(context, LockScreenService.class));
                }
            }

        }
    }

    private boolean check(){
        return !isCallActive() &
                (LockScreenService.isRunning & !"".equals(LockScreenService.getPreferencesService().getReal()));
    }

    public boolean isCallActive(){
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }
}
