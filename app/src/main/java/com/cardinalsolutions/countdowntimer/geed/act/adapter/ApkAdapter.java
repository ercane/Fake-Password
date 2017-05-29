package com.cardinalsolutions.countdowntimer.geed.act.adapter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;

import java.util.List;


public class ApkAdapter extends ArrayAdapter<PackageInfo>{
    List<PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;

    public ApkAdapter(Activity context, List<PackageInfo> packageList,
                      PackageManager packageManager){
        super(context, R.layout.apklist_item, packageList);
        this.context = context;
        this.packageList = packageList;
        this.packageManager = packageManager;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.apklist_item, null);
        }

        TextView apkName = (TextView) convertView.findViewById(R.id.appname);
        CheckBox swEnable = (CheckBox) convertView.findViewById(R.id.cbStatus);
        try {
            final PackageInfo packageInfo = (PackageInfo) getItem(position);
            Drawable appIcon = packageManager
                    .getApplicationIcon(packageInfo.applicationInfo);
            String appName = packageManager.getApplicationLabel(
                    packageInfo.applicationInfo).toString();
            appIcon.setBounds(0, 0, 40, 40);
            apkName.setCompoundDrawables(appIcon, null, null, null);
            apkName.setCompoundDrawablePadding(15);
            apkName.setText(appName);

            swEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){

                }
            });
            swEnable.setChecked(LockScreenService.getPreferencesService().isPackageSelected(packageInfo.packageName));
            swEnable.setText("");
            swEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                    if (isChecked) {
                        LockScreenService.getPreferencesService().addApp(packageInfo.packageName);
                    } else {
                        LockScreenService.getPreferencesService().removeApp(packageInfo.packageName);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
