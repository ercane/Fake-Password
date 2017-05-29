package com.cardinalsolutions.countdowntimer.geed.shared;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesService{
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SharedPreferencesService(SharedPreferences preferences){
        this.preferences = preferences;
        this.editor = preferences.edit();
    }


    public Boolean isKnoxEnabled(){
        return preferences.getBoolean(SharedPreferencesKeys.IS_KNOX_ENABLED, false);
    }

    public void setIsKnoxEnabled(Boolean state){
        editor.putBoolean(SharedPreferencesKeys.IS_KNOX_ENABLED, state);
        editor.commit();
    }

    public String getBckgrndSet(){
        return preferences.getString(SharedPreferencesKeys.BCKGRND, "");
    }

    public void setBckgrndSet(String state){
        editor.putString(SharedPreferencesKeys.BCKGRND, state);
        editor.commit();
    }

    public List<String> getListApps(){
        String json = preferences.getString(SharedPreferencesKeys.LIST_APPS, "");
        if ("".equals(json)) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(json, List.class);
        }
    }

    public void setListApps(List<String> list){
        editor.putString(SharedPreferencesKeys.LIST_APPS, new Gson().toJson(list));
        editor.commit();
    }

    public boolean addApp(String packageName){
        try {
            List<String> listApps = getListApps();
            if (!listApps.contains(packageName)) {
                listApps.add(packageName);
            }

            setListApps(listApps);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeApp(String packageName){
        try {
            List<String> listApps = getListApps();
            if (listApps.contains(packageName)) {
                listApps.remove(packageName);
            }

            setListApps(listApps);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPackageSelected(String packageName){
        try {
            List<String> listApps = getListApps();
            if (listApps.contains(packageName)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getReal(){
        return preferences.getString(SharedPreferencesKeys.REAL_PASS, "");
    }

    public void setReal(String s){
        editor.putString(SharedPreferencesKeys.REAL_PASS, s);
        editor.commit();
    }

    public String getFake(){
        return preferences.getString(SharedPreferencesKeys.FAKE_PASS, "");
    }

    public void setFake(String s){
        editor.putString(SharedPreferencesKeys.FAKE_PASS, s);
        editor.commit();
    }

    public String getKey(){
        return preferences.getString(SharedPreferencesKeys.EML, "");
    }

    public void setKey(String s){
        editor.putString(SharedPreferencesKeys.EML, s);
        editor.commit();
    }

    public Long getEnrollmentDate(){
        return preferences.getLong(SharedPreferencesKeys.ENROLLMENT_DATE, 0);
    }

    public void setEnrollmentDate(Long s){
        editor.putLong(SharedPreferencesKeys.ENROLLMENT_DATE, s);
        editor.commit();
    }

    public Long getOpenDate(){
        return preferences.getLong(SharedPreferencesKeys.OPEN_DATE, 0);
    }

    public void setOpenDate(Long s){
        editor.putLong(SharedPreferencesKeys.OPEN_DATE, s);
        editor.commit();
    }

    public Long getCountdownDate(){
        return preferences.getLong(SharedPreferencesKeys.COUNTDOWN_DATE, 0);
    }

    public void setCountdownDate(Long s){
        editor.putLong(SharedPreferencesKeys.COUNTDOWN_DATE, s);
        editor.commit();
    }

    public int getFailedCount(){
        return preferences.getInt(SharedPreferencesKeys.FAILED_COUNT, 0);
    }

    public void setFailedCount(int s){
        editor.putInt(SharedPreferencesKeys.FAILED_COUNT, s);
        editor.commit();
    }

    public int getCurrentFailedCount(){
        return preferences.getInt(SharedPreferencesKeys.CURRENT_FAILED_COUNT, 0);
    }

    public void setCurrentFailedCount(int s){
        editor.putInt(SharedPreferencesKeys.CURRENT_FAILED_COUNT, s);
        editor.commit();
    }


}
