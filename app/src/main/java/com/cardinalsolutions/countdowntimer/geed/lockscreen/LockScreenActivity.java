package com.cardinalsolutions.countdowntimer.geed.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.crypt.DbEncryptOperations;
import com.cardinalsolutions.countdowntimer.geed.knox.KnoxUtils;
import com.cardinalsolutions.countdowntimer.geed.util.OnSwipeTouchListener;
import com.cardinalsolutions.countdowntimer.geed.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * if the screen is locked, this Activity will show.
 *
 * @author Andy
 */
public class LockScreenActivity extends Activity{

    public static boolean isLocked = false;
    private TextView tv_time, tvError;
    private EditText etPass;
    private RelativeLayout mainLayout;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_lock_screen);

        isLocked = true;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        etPass = (EditText) findViewById(R.id.etPassword);
        tvError = (TextView) findViewById(R.id.tvError);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setText(TimeUtil.getTime());

        etPass.setVisibility(View.GONE);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(LockScreenActivity.this){
            public void onSwipeTop(){
                swipe();
            }

            public void onSwipeRight(){
                swipe();
            }

            public void onSwipeLeft(){
                swipe();
            }

            public void onSwipeBottom(){
                swipe();
            }

        });

        if (!TextUtils.isEmpty(LockScreenService.getPreferencesService().getBckgrndSet())) {
            try {
                Drawable fromPath = Drawable.createFromPath(LockScreenService.getPreferencesService().getBckgrndSet());
                mainLayout.setBackground(fromPath);
                tv_time.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mainLayout.setBackgroundResource(R.drawable.sam_wal);
        }


        etPass.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    checkPass();
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });
        try {
            startService(new Intent(this, LockScreenService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPass(){
        if (!TextUtils.isEmpty(etPass.getText())) {
            String pass = etPass.getText().toString();
            String fake = LockScreenService.getPreferencesService().getFake();
            String real = LockScreenService.getPreferencesService().getReal();
            try {
                byte[] decrypt = DbEncryptOperations.decrypt(Base64.decode(real.getBytes(), Base64.DEFAULT));
                real = new String(decrypt);
            } catch (Exception e) {
                real = e.getMessage();
            }

            try {
                byte[] decrypt = DbEncryptOperations.decrypt(Base64.decode(fake.getBytes(), Base64.DEFAULT));
                fake = new String(decrypt);
            } catch (Exception e) {
                fake = e.getMessage();
            }
            if (!TextUtils.isEmpty(fake) && !TextUtils.isEmpty(real)) {
                if (fake.equalsIgnoreCase(pass)) {
                    showMsg(R.string.pass_fake);
                    fakeOperations();
                    isLocked = false;
                    showMsg(R.string.pass_valid);
                    finish();
                } else if (real.toString().equalsIgnoreCase(pass.toString())) {
                    isLocked = false;
                    showMsg(R.string.pass_valid);
                    LockScreenService.getPreferencesService().setCurrentFailedCount(0);
                    KnoxUtils.setHomeButtonState(true);
                    finish();
                } else {
                    int cfc = LockScreenService.getPreferencesService().getCurrentFailedCount();
                    cfc++;
                    if (LockScreenService.getPreferencesService().getFailedCount() != 0) {
                        if (cfc < LockScreenService.getPreferencesService().getFailedCount()) {
                            LockScreenService.getPreferencesService().setCurrentFailedCount(cfc);
                            showError(R.string.pass_wrong_error);
                        } else {
                            fakeOperations();
                            isLocked = false;
                            showMsg(R.string.pass_valid);
                            finish();
                        }
                    } else {
                        showError(R.string.pass_wrong_error);
                    }
                }
            } else {
                isLocked = false;
                showMsg(R.string.pass_valid);
                KnoxUtils.setHomeButtonState(true);
                finish();
            }
        } else {
            showError(R.string.pass_null_error);
        }
    }

    private void swipe(){
        virbate();
        if (TextUtils.isEmpty(LockScreenService.getPreferencesService().getReal())) {
            KnoxUtils.setHomeButtonState(true);
            isLocked = false;
            finish();
        }

        if (etPass.getVisibility() == View.GONE) {
            etPass.setVisibility(View.VISIBLE);
            etPass.requestFocus();
            imm.showSoftInput(etPass, 0);
        }
    }

    private void clearApp(){
        KnoxUtils.setAdminRemovable(true);
        LockScreenService.getPreferencesService().setFailedCount(0);
        LockScreenService.getPreferencesService().setReal("");
        LockScreenService.getPreferencesService().setFake("");
        LockScreenService.getPreferencesService().setCurrentFailedCount(0);
        LockScreenService.getPreferencesService().setIsKnoxEnabled(false);
        KnoxUtils.removeSelfAdmin(getApplicationContext());
    }

    private void showMsg(int msg){
        String msgStr = getApplicationContext().getString(msg);
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.GREEN);
        tvError.setText(msgStr);
    }

    private void showError(int msg){
        String msgStr = getApplicationContext().getString(msg);
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.RED);
        tvError.setText(msgStr);
    }

    private void fakeOperations(){
        List<String> apps = LockScreenService.getPreferencesService().getListApps();
        boolean b = false;
        for (String app : apps) {
            b = KnoxUtils.removeApp(app);
        }
        LockScreenService.getPreferencesService().setListApps(new ArrayList<String>());
        clearApp();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            // Key code constant: Home key. This key is handled by the framework and is never delivered to applications.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed(){
        //return;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * virbate means that the screen is unlocked success
     */
    private void virbate(){
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
