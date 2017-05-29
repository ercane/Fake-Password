package com.cardinalsolutions.countdowntimer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;
import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesKeys;
import com.cardinalsolutions.countdowntimer.geed.shared.SharedPreferencesService;
import com.cardinalsolutions.countdowntimer.geed.util.OnSwipeTouchListener;
import com.cardinalsolutions.countdowntimer.geed.views.DateTimeSetView;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.Date;

/**
 * Countdown Timer
 * <p>
 * CountdownTimerActivity is the main activity of this application.  To configure  is a utility class that allows the developer to turn off logging by setting the LOGGING
 * flag to false prior to generating an APK.
 *
 * @author Shane King
 *         28 Aug 2014
 *         10:00
 */
public class CountdownTimerActivity extends Activity{

    private static final String TAG = "CountdownTimer";
    private static int CLICK_COUNT = 0;
    // Timer setup
    Time conferenceTime = new Time(Time.getCurrentTimezone());
    int hour = 22;
    int minute = 33;
    int second = 0;
    int monthDay = 28;
    // month is zero based...7 == August
    int month = 7;
    int year;
    //private TextView mCountdownNote;
    private ProgressWheel mDaysWheel;
    private TextView mDaysLabel;
    private ProgressWheel mHoursWheel;
    private TextView mHoursLabel;
    private ProgressWheel mMinutesWheel;
    private TextView mMinutesLabel;
    private ProgressWheel mSecondsWheel;
    private TextView mSecondsLabel;
    private Button btnSetDateTime;
    private ImageView ivBird, ivLogo;
    // Values displayed by the timer
    private int mDisplayDays;
    private int mDisplayHours;
    private int mDisplayMinutes;
    private int mDisplaySeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        LockScreenService.setPreferencesService(new SharedPreferencesService
                (getSharedPreferences(SharedPreferencesKeys.ROOT, Context.MODE_PRIVATE)));
        if (LockScreenService.getPreferencesService().getOpenDate() == 0) {
            Date date = new Date();
        }
        configureViews();
        configureConferenceDate();
        ivLogo.setOnTouchListener(new OnSwipeTouchListener(CountdownTimerActivity.this){
            boolean isBottom = false;
            boolean isRight = false;

            @Override
            public void onSwipeLeft(){
                isBottom = false;
                isRight = false;
            }

            @Override
            public void onSwipeTop(){
                isBottom = false;
                isRight = false;
            }

            @Override
            public void onSwipeBottom(){
                isBottom = true;
                isRight = false;
            }

            @Override
            public void onSwipeRight(){
                if (isBottom && !isRight) {
                    DateTimeSetView addToView = new DateTimeSetView(CountdownTimerActivity.this, true);
                    AlertDialog.Builder builder = addToView.getBuilder();
                    AlertDialog dialog = builder.create();
                    addToView.setDialog(dialog);
                    dialog.show();
                }
                isBottom = false;
            }

        });
        ivBird.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CLICK_COUNT++;
                Handler handler = new Handler();
                Runnable r = new Runnable(){
                    @Override
                    public void run(){
                        Log.e("COUNT", CLICK_COUNT + "");
                        if (CLICK_COUNT == 4) {
                            DateTimeSetView addToView = new DateTimeSetView(CountdownTimerActivity.this, true);
                            AlertDialog.Builder builder = addToView.getBuilder();
                            AlertDialog dialog = builder.create();
                            addToView.setDialog(dialog);
                            dialog.show();
                        }
                        CLICK_COUNT = 0;
                    }
                };
                if (CLICK_COUNT != 4) {
                    handler.postDelayed(r, 800);
                }

            }
        });

        btnSetDateTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DateTimeSetView addToView = new DateTimeSetView(CountdownTimerActivity.this);
                AlertDialog.Builder builder = addToView.getBuilder();
                AlertDialog dialog = builder.create();
                addToView.setDialog(dialog);
                dialog.show();
            }
        });
    }

    private void configureViews(){

        this.conferenceTime.setToNow();
        this.year = conferenceTime.year;

        //this.mCountdownNote = (TextView) findViewById(R.id.activity_countdown_timer_note);
        this.mDaysWheel = (ProgressWheel) findViewById(R.id.activity_countdown_timer_days);
        this.mHoursWheel = (ProgressWheel) findViewById(R.id.activity_countdown_timer_hours);
        this.mMinutesWheel = (ProgressWheel) findViewById(R.id.activity_countdown_timer_minutes);
        this.mSecondsWheel = (ProgressWheel) findViewById(R.id.activity_countdown_timer_seconds);
        this.mDaysLabel = (TextView) findViewById(R.id.activity_countdown_timer_days_text);
        this.mHoursLabel = (TextView) findViewById(R.id.activity_countdown_timer_hours_text);
        this.mMinutesLabel = (TextView) findViewById(R.id.activity_countdown_timer_minutes_text);
        this.mSecondsLabel = (TextView) findViewById(R.id.activity_countdown_timer_seconds_text);
        btnSetDateTime = (Button) findViewById(R.id.btnSet);
        ivLogo = (ImageView) findViewById(R.id.activity_cardinal_logo);
        ivBird = (ImageView) findViewById(R.id.activity_cardinal_bird);

    }

    private void configureConferenceDate(){
        conferenceTime.set(second, minute, hour, monthDay, month, year);
        conferenceTime.normalize(true);
        long confMillis = conferenceTime.toMillis(true);
        if (LockScreenService.getPreferencesService().getCountdownDate() != 0) {
            confMillis = LockScreenService.getPreferencesService().getCountdownDate();
        }
        Date now = new Date();
        Time nowTime = new Time(Time.getCurrentTimezone());
        nowTime.setToNow();
        nowTime.normalize(true);
        //long nowMillis = nowTime.toMillis(true);
        long nowMillis = now.getTime();

        long milliDiff = confMillis - nowMillis;

        new CountDownTimer(milliDiff, 1000){

            @Override
            public void onTick(long millisUntilFinished){
                // decompose difference into days, hours, minutes and seconds
                CountdownTimerActivity.this.mDisplayDays = (int) ((millisUntilFinished / 1000) / 86400);
                CountdownTimerActivity.this.mDisplayHours = (int) (((millisUntilFinished / 1000) - (CountdownTimerActivity.this.mDisplayDays * 86400)) / 3600);
                CountdownTimerActivity.this.mDisplayMinutes = (int) (((millisUntilFinished / 1000) - ((CountdownTimerActivity.this.mDisplayDays * 86400) + (CountdownTimerActivity.this.mDisplayHours * 3600))) / 60);
                CountdownTimerActivity.this.mDisplaySeconds = (int) ((millisUntilFinished / 1000) % 60);

                CountdownTimerActivity.this.mDaysWheel.setText(String.valueOf(CountdownTimerActivity.this.mDisplayDays));
                CountdownTimerActivity.this.mDaysWheel.setProgress(CountdownTimerActivity.this.mDisplayDays);

                CountdownTimerActivity.this.mHoursWheel.setText(String.valueOf(CountdownTimerActivity.this.mDisplayHours));
                CountdownTimerActivity.this.mHoursWheel.setProgress(CountdownTimerActivity.this.mDisplayHours * 15);

                CountdownTimerActivity.this.mMinutesWheel.setText(String.valueOf(CountdownTimerActivity.this.mDisplayMinutes));
                CountdownTimerActivity.this.mMinutesWheel.setProgress(CountdownTimerActivity.this.mDisplayMinutes * 6);

                Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
                an.setFillAfter(true);

                CountdownTimerActivity.this.mSecondsWheel.setText(String.valueOf(CountdownTimerActivity.this.mDisplaySeconds));
                CountdownTimerActivity.this.mSecondsWheel.setProgress(CountdownTimerActivity.this.mDisplaySeconds * 6);
            }

            @Override
            public void onFinish(){
                //TODO: this is where you would launch a subsequent activity if you'd like.  I'm currently just setting the seconds to zero
                Logger.d(TAG, "Timer Finished...");
                CountdownTimerActivity.this.mSecondsWheel.setText("0");
                CountdownTimerActivity.this.mSecondsWheel.setProgress(0);
            }
        }.start();
    }
}
