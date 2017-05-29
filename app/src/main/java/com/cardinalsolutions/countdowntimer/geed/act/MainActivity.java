package com.cardinalsolutions.countdowntimer.geed.act;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.crypt.DbEncryptOperations;
import com.cardinalsolutions.countdowntimer.geed.knox.KnoxUtils;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;
import com.cardinalsolutions.countdowntimer.geed.receivers.AdminReceiver;
import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesKeys;
import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesService;


public class MainActivity extends Activity{

    private static final int RESULT_ENABLE = 111;
    private static Handler adminEnabledHandler;
    private Button btnActivate, btnFakeSet, btnInfo;
    private EditText etCode, etPass;
    private Button btnCode, btnCodeFirst, btnEnter;
    private LinearLayout passLayout, menuLayout, codeLayout;
    private DevicePolicyManager dpm;
    private ComponentName componentName;

    public static Handler getAdminEnabledHandler(){
        return adminEnabledHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, LockScreenService.class));
        checkWritePermission();
        passLayout = (LinearLayout) findViewById(R.id.passLayout);
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        etPass = (EditText) findViewById(R.id.pass);
        btnEnter = (Button) findViewById(R.id.enterBtn);
        btnActivate = (Button) findViewById(R.id.btnAdminAct);
        btnFakeSet = (Button) findViewById(R.id.btnFakeSet);
        btnInfo = (Button) findViewById(R.id.btnInfo);
        codeLayout = (LinearLayout) findViewById(R.id.codeLayout);
        btnCode = (Button) findViewById(R.id.btnCode);
        btnCodeFirst = (Button) findViewById(R.id.btnCodeFirst);
        etCode = (EditText) findViewById(R.id.etCode);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(MainActivity.this, AdminReceiver.class);
        LockScreenService.setPreferencesService(new SharedPreferencesService
                (getSharedPreferences(SharedPreferencesKeys.ROOT, Context.MODE_PRIVATE)));

        if (TextUtils.isEmpty(LockScreenService.getPreferencesService().getKey())) {
            LockScreenService.getPreferencesService().setKey(getString(R.string.key));
        }


        if (dpm.isAdminActive(componentName)) {
            if (LockScreenService.getPreferencesService().isKnoxEnabled()) {
                btnActivate.setEnabled(false);
                btnFakeSet.setEnabled(true);
            } else {
                btnActivate.setEnabled(true);
                btnFakeSet.setEnabled(false);
            }
        } else {
            btnActivate.setEnabled(true);
            btnFakeSet.setEnabled(false);
        }

        if (!TextUtils.isEmpty(LockScreenService.getPreferencesService().getReal())) {
            passLayout.setVisibility(View.VISIBLE);
            menuLayout.setVisibility(View.GONE);
        } else {
            passLayout.setVisibility(View.GONE);
            menuLayout.setVisibility(View.VISIBLE);
        }

        btnActivate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!dpm.isAdminActive(componentName)) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
                    startActivityForResult(intent, RESULT_ENABLE);
                } else if (!LockScreenService.getPreferencesService().isKnoxEnabled()) {
                    String key = LockScreenService.getPreferencesService().getKey();
                    if (!TextUtils.isEmpty(key)) {
                        KnoxUtils.licenceElm(getApplicationContext(), key, getPackageName());
                    } else {
                        Toast.makeText(getApplicationContext(), "EML key doesn't exist", Toast.LENGTH_LONG).show();
                    }
                } else {
                }
            }
        });

        btnFakeSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (codeLayout.getVisibility() == View.GONE) {
                    codeLayout.setVisibility(View.VISIBLE);
                } else {
                    codeLayout.setVisibility(View.GONE);
                }
            }
        });

        btnCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!TextUtils.isEmpty(etCode.getText())) {
                    LockScreenService.getPreferencesService().setKey(etCode.getText().toString());
                    codeLayout.setVisibility(View.GONE);
                }
            }
        });

        btnCodeFirst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LockScreenService.getPreferencesService().setKey(getString(R.string.key));
                codeLayout.setVisibility(View.GONE);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (TextUtils.isEmpty(etPass.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.pass_null_error, Toast.LENGTH_LONG).show();
                    return;
                }

                String pass = etPass.getText().toString();

                String real = LockScreenService.getPreferencesService().getReal();
                try {
                    byte[] decrypt = DbEncryptOperations.decrypt(Base64.decode(real.getBytes(), Base64.DEFAULT));
                    real = new String(decrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (real.equals(pass)) {
                    passLayout.setVisibility(View.GONE);
                    menuLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pass_wrong_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        adminEnabledHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                btnActivate.setEnabled(false);
                btnFakeSet.setEnabled(true);

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RESULT_ENABLE) {
            try {
                String key = LockScreenService.getPreferencesService().getKey();
                if (!TextUtils.isEmpty(key)) {
                    KnoxUtils.licenceElm(getApplicationContext(), key, getPackageName());
                } else {
                    Toast.makeText(getApplicationContext(), "EML key doesn't exist", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }

    private void checkWritePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }
    }
}
