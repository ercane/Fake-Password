package com.cardinalsolutions.countdowntimer.geed.knox;

import android.app.admin.DevicePolicyManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;
import com.cardinalsolutions.countdowntimer.geed.receivers.AdminReceiver;

import java.util.List;


public class KnoxUtils{

    private static EnterpriseDeviceManager edm;
    private static EnterpriseLicenseManager elm;

    public static boolean licenceElm(Context context, String emlKey, String packageName){
        try {
            if (elm == null) {
                elm = EnterpriseLicenseManager.getInstance(context);
            }
            if (emlKey != null) {
                if (packageName == null) {
                    elm.activateLicense(emlKey);
                } else {
                    elm.activateLicense(emlKey);
                }
                //Log.v("KNOX", "Licenced successfully");
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("KNOX", "License failed. Exception: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeApp(String packageName){
        try {
            boolean admin = checkAndRemoveAdmin(packageName);
            return getEdm().getApplicationPolicy().uninstallApplication(packageName, false);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkAndRemoveAdmin(String packageName){
        DevicePolicyManager dpm = (DevicePolicyManager) LockScreenService.getContext().getSystemService(Context
                .DEVICE_POLICY_SERVICE);
        List<ComponentName> activeAdmins = dpm.getActiveAdmins();
        if (activeAdmins != null) {
            for (ComponentName cn : activeAdmins) {
                if (cn.getPackageName().equals(packageName)) {
                    dpm.removeActiveAdmin(cn);
                    return true;
                }
            }
        }
        return false;
    }


    public static EnterpriseDeviceManager getEdm(){
        if (edm == null) {
            edm = new EnterpriseDeviceManager(LockScreenService.getContext());
        }
        return edm;
    }

    public static void removeSelfAdmin(Context context){
        try {
            ComponentName componentName = new ComponentName(LockScreenService.getContext(), AdminReceiver.class);
            DevicePolicyManager dpm = (DevicePolicyManager) LockScreenService.getContext().getSystemService(Context
                    .DEVICE_POLICY_SERVICE);
            dpm.removeActiveAdmin(componentName);
            getEdm().getApplicationPolicy().uninstallApplication(context.getPackageName(), false);
        } catch (Exception e) {
        }
    }

    public static boolean setHomeButtonState(boolean state){
        try {
            boolean b = getEdm().getRestrictionPolicy().setHomeKeyState(state);
            return b;
        } catch (Exception e) {
            Log.e("KNOX", e.getMessage() + "");
            return false;
        }
    }

    public static boolean getAdminRemovable(){
        try {
            return getEdm().getAdminRemovable();
        } catch (Exception e) {
            return false;
        }
    }

    public static void setAdminRemovable(boolean adminRemovable){
        try {
            getEdm().setAdminRemovable(adminRemovable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
