package com.cardinalsolutions.countdowntimer.geed.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.act.MainActivity;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mree on 11.04.2017.
 */

public class DateTimeSetView extends BaseView{
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd:HH.MM");
    private static Button btnSetDate, btnSetTime, btnSave;
    private static boolean isDateSet, isTimeSet;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dateString = "";
    private TextView tvError;
    private boolean isOpen;
    private TextView tvHeader;
    private Calendar resultCalendar = Calendar.getInstance();

    public DateTimeSetView(Context context){
        super(context);
        isOpen = false;
        init();
    }

    public DateTimeSetView(Context context, boolean isOpen){
        super(context);
        this.isOpen = isOpen;
        init();
    }

    public static Button getBtnSetDate(){
        return btnSetDate;
    }

    public static void setBtnSetDate(Button btnSetDate){
        DateTimeSetView.btnSetDate = btnSetDate;
    }

    public static Button getBtnSetTime(){
        return btnSetTime;
    }

    public static void setBtnSetTime(Button btnSetTime){
        DateTimeSetView.btnSetTime = btnSetTime;
    }

    public static Button getBtnSave(){
        return btnSave;
    }

    public static void setBtnSave(Button btnSave){
        DateTimeSetView.btnSave = btnSave;
    }

    public static boolean isDateSet(){
        return isDateSet;
    }

    public static void setIsDateSet(boolean isDateSet){
        DateTimeSetView.isDateSet = isDateSet;
    }

    public static boolean isTimeSet(){
        return isTimeSet;
    }

    public static void setIsTimeSet(boolean isTimeSet){
        DateTimeSetView.isTimeSet = isTimeSet;
    }

    @Override
    void init(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View mainView = inflater.inflate(R.layout.layout_datetimeset, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(mainView);

        tvError = (TextView) mainView.findViewById(R.id.tvError);
        tvHeader = (TextView) mainView.findViewById(R.id.tvHeader);

        btnSetDate = (Button) mainView.findViewById(R.id.btnSetDate);
        btnSetTime = (Button) mainView.findViewById(R.id.btnSetTime);
        btnSave = (Button) mainView.findViewById(R.id.btnSave);

        btnSetDate.setEnabled(true);
        btnSetTime.setEnabled(false);
        btnSave.setEnabled(false);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        if (isOpen) {
            tvHeader.setText("Fake Set");
        }

        btnSetDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener(){

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth){
                                btnSetTime.setEnabled(true);
                                if (monthOfYear < 9) {
                                    dateString += year + ".0" + (monthOfYear + 1) + "." + dayOfMonth;
                                } else {
                                    dateString += year + "." + (monthOfYear + 1) + "." + dayOfMonth;
                                }
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                showMsg(dateString);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute){
                                String hour = "";
                                String m = "";

                                if (hourOfDay < 10) {
                                    hour += "0" + hourOfDay;
                                } else {
                                    hour += hourOfDay;
                                }

                                if (minute > 9) {
                                    m += minute;
                                } else {
                                    m = "0" + minute;
                                }

                                dateString += ":" + hour + "." + m;
                                btnSave.setEnabled(true);
                                showMsg(dateString);
                                mHour = hourOfDay;
                                mMinute = minute;
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Date date = null;
                try {
                    date = SDF.parse(dateString);
                    resultCalendar.set(mYear, mMonth, mDay, mHour, mMinute);
                    resultCalendar.set(Calendar.SECOND, 0);
                    date = resultCalendar.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date now = new Date();
                if (date == null || date.getTime() < now.getTime()) {
                    showError(R.string.date_not_suitable);
                } else if (Math.abs(LockScreenService.getPreferencesService().getOpenDate() - date.getTime()) < 60000) {
                    if (!isOpen) {
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(i);
                        dialog.dismiss();
                    }
                } else {
                    if (isOpen) {
                        LockScreenService.getPreferencesService().setOpenDate(date.getTime());
                    } else {
                        LockScreenService.getPreferencesService().setCountdownDate(date.getTime());
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    private void showMsg(String msg){
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.GREEN);
        tvError.setText(msg);
    }

    private void showError(int msg){
        String msgStr = getContext().getString(msg);
        tvError.setVisibility(View.VISIBLE);
        tvError.setTextColor(Color.RED);
        tvError.setText(msgStr);
    }
}
