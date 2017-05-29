package com.cardinalsolutions.countdowntimer.geed.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public abstract class BaseView{
    protected Context context;
    protected AlertDialog.Builder builder;
    protected AlertDialog dialog;

    public BaseView(Context context){
        this.context = context;
    }

    abstract void init();

    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public AlertDialog.Builder getBuilder(){
        return builder;
    }

    public void setBuilder(AlertDialog.Builder builder){
        this.builder = builder;
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    public void setDialog(AlertDialog dialog){
        this.dialog = dialog;
    }
}
