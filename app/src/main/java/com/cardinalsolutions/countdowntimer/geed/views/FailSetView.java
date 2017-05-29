package com.cardinalsolutions.countdowntimer.geed.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;


public class FailSetView extends BaseView{

    public FailSetView(Context context){
        super(context);
        init();
    }

    @Override
    void init(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View mainView = inflater.inflate(R.layout.layout_edit_key, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(mainView);

        final EditText etValue = (EditText) mainView.findViewById(R.id.etValue);
        final Button btnSet = (Button) mainView.findViewById(R.id.enterBtn);

        int failedCount = LockScreenService.getPreferencesService().getFailedCount();
        etValue.setText(failedCount + "");
        btnSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (TextUtils.isEmpty(etValue.getText())) {
                    Toast.makeText(context, R.string.pass_null_error, Toast.LENGTH_LONG).show();
                    return;
                }

                Integer count = Integer.parseInt(etValue.getText().toString());
                if (count <= 3) {
                    Toast.makeText(context, "Please set bigger than 3", Toast.LENGTH_LONG).show();
                } else {
                    LockScreenService.getPreferencesService().setFailedCount(count);
                    dialog.dismiss();
                }
            }
        });

    }
}
