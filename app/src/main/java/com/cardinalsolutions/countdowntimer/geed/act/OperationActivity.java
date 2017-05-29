package com.cardinalsolutions.countdowntimer.geed.act;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.act.adapter.ApkAdapter;
import com.cardinalsolutions.countdowntimer.geed.util.AppData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class OperationActivity extends Activity implements AdapterView.OnItemClickListener{

    PackageManager packageManager;
    ListView apkList;
    ApkAdapter apkAdapter;
    List<PackageInfo> packageList1;
    private Button btnOne, btnTwo;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

        packageList1 = new ArrayList<PackageInfo>();

        /*To filter out System apps*/
        for (PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            if (!b) {
                packageList1.add(pi);
            }
        }
        apkList = (ListView) findViewById(R.id.applist);
        Comparator<PackageInfo> comparator = new Comparator<PackageInfo>(){
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs){
                return lhs.packageName.compareTo(rhs.packageName);
            }
        };
        Collections.sort(packageList1, comparator);
        apkAdapter = new ApkAdapter(this, packageList1, packageManager);
        apkList.setAdapter(apkAdapter);
        apkList.setOnItemClickListener(this);

    }

    /**
     * Return whether the given PackgeInfo represents a system package or not.
     * User-installed packages (Market or otherwise) should not be denoted as
     * system packages.
     *
     * @param pkgInfo
     * @return boolean
     */
    private boolean isSystemPackage(PackageInfo pkgInfo){
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long row){
        PackageInfo packageInfo = (PackageInfo) parent
                .getItemAtPosition(position);
        AppData appData = (AppData) getApplicationContext();
        appData.setPackageInfo(packageInfo);

        Intent appInfo = new Intent(getApplicationContext(), ApkInfo.class);
        startActivity(appInfo);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //apkAdapter.clear();
        //apkAdapter.addAll(packageList1);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        //apkAdapter.clear();
        // apkAdapter.addAll(packageList1);
    }
}
