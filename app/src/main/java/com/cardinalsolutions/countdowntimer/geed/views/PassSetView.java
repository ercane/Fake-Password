package com.cardinalsolutions.countdowntimer.geed.views;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.act.SettingsActivity;
import com.cardinalsolutions.countdowntimer.geed.crypt.DbEncryptOperations;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;


public class PassSetView extends BaseView{
    private static final String TAG = PassSetView.class.getSimpleName();
    private static int PASS_STRENTH = 6;
    private LinearLayout createPass, enterPass;
    private EditText newPass1, newPass2, pass;
    private TextView tvError;
    private Button createBtn, enterBtn;
    private int flag = 0;

    public PassSetView(Context context, int flag){
        super(context);
        this.flag = flag;
        init();
    }

    public void init(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View mainView = inflater.inflate(R.layout.layout_pass_set, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(mainView);

        tvError = (TextView) mainView.findViewById(R.id.tvError);
        createPass = (LinearLayout) mainView.findViewById(R.id.createPass);
        enterPass = (LinearLayout) mainView.findViewById(R.id.enterPass);
        newPass1 = (EditText) mainView.findViewById(R.id.newPass1);
        newPass2 = (EditText) mainView.findViewById(R.id.newPass2);
        pass = (EditText) mainView.findViewById(R.id.pass);
        createBtn = (Button) mainView.findViewById(R.id.createBtn);
        enterBtn = (Button) mainView.findViewById(R.id.enterBtn);

        if (TextUtils.isEmpty(getPassword())) {
            enterPass.setVisibility(View.GONE);
            createPass.setVisibility(View.VISIBLE);
        } else {
            createPass.setVisibility(View.GONE);
            enterPass.setVisibility(View.VISIBLE);
        }

        createBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                tvError.setVisibility(View.GONE);
                if (newPass1.getText() == null || newPass2.getText() == null) {
                    showError(R.string.pass_null_error);
                } else if (!newPass1.getText().toString().equals(newPass2.getText().toString())) {
                    showError(R.string.pass_same_error);
                } else if (newPass1.getText().toString().length() < PASS_STRENTH) {
                    showError(R.string.pass_length_error);
                } else {
                    savePass();
                    showMsg(R.string.pass_create_msg);
                    SettingsActivity.getCheckAgainHandler().sendEmptyMessage(25);
                    dialog.dismiss();
                }
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                tvError.setVisibility(View.GONE);
                if (pass.getText() == null) {
                    showError(R.string.pass_null_error);
                } else {
                    try {
                        String p = getPassword();
                        byte[] decrypt = DbEncryptOperations.decrypt(Base64.decode(p.getBytes(), Base64.DEFAULT));
                        if (pass.getText().toString().equals(new String(decrypt))) {
                            enterPass.setVisibility(View.GONE);
                            createPass.setVisibility(View.VISIBLE);
                        } else {
                            showError(R.string.pass_wrong_error);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });

    }

    private void showMsg(int msg){
        String msgStr = getContext().getString(msg);
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.GREEN);
        tvError.setText(msgStr);
    }

    private void showError(int msg){
        String msgStr = getContext().getString(msg);
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.RED);
        tvError.setText(msgStr);
    }

    private void savePass(){
        String p = newPass1.getText().toString();
        try {
            byte[] encrypt = DbEncryptOperations.encrypt(p.getBytes());
            p = Base64.encodeToString(encrypt, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag == 0) {
            LockScreenService.getPreferencesService().setReal(p);
        } else {
            LockScreenService.getPreferencesService().setFake(p);
        }
    }

    private String getPassword(){
        if (flag == 0) {
            return LockScreenService.getPreferencesService().getReal();
        } else {
            return LockScreenService.getPreferencesService().getFake();
        }
    }

}
