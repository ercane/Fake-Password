package com.cardinalsolutions.countdowntimer.geed.act;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.cardinalsolutions.countdowntimer.R;
import com.cardinalsolutions.countdowntimer.geed.lockscreen.LockScreenService;
import com.cardinalsolutions.countdowntimer.geed.views.FailSetView;
import com.cardinalsolutions.countdowntimer.geed.views.PassSetView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SettingsActivity extends AppCompatActivity{

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static Handler checkAgainHandler;
    private Button btnRealSet, btnFakeSet, btnFailSet, btnAppSet, btnSetBckrnd;
    private Switch swNot;

    public static Handler getCheckAgainHandler(){
        return checkAgainHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btnRealSet = (Button) findViewById(R.id.btnRealSet);
        btnRealSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PassSetView addToView = new PassSetView(SettingsActivity.this, 0);
                AlertDialog.Builder builder = addToView.getBuilder();
                AlertDialog dialog = builder.create();
                addToView.setDialog(dialog);
                dialog.show();
            }
        });

        btnFakeSet = (Button) findViewById(R.id.btnFakeSet);
        btnFakeSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PassSetView addToView = new PassSetView(SettingsActivity.this, 1);
                AlertDialog.Builder builder = addToView.getBuilder();
                AlertDialog dialog = builder.create();
                addToView.setDialog(dialog);
                dialog.show();
            }
        });

        btnFailSet = (Button) findViewById(R.id.btnFailSet);
        btnFailSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FailSetView addToView = new FailSetView(SettingsActivity.this);
                AlertDialog.Builder builder = addToView.getBuilder();
                AlertDialog dialog = builder.create();
                addToView.setDialog(dialog);
                dialog.show();

            }
        });

        btnAppSet = (Button) findViewById(R.id.btnAppSet);
        btnAppSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(SettingsActivity.this, OperationActivity.class);
                startActivity(i);

            }
        });

        btnSetBckrnd = (Button) findViewById(R.id.btnBckrnd);
        btnSetBckrnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (!TextUtils.isEmpty(LockScreenService.getPreferencesService().getBckgrndSet())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("Background");
                    builder.setMessage("There is already image. Do you want to remove and choose new one?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    try {
                                        File f = new File(LockScreenService.getPreferencesService().getBckgrndSet());
                                        boolean delete = f.delete();
                                        LockScreenService.getPreferencesService().setBckgrndSet("");
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/jpeg");
                                        startActivityForResult(photoPickerIntent, 100);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.cancel();
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/jpeg");
                    startActivityForResult(photoPickerIntent, 100);
                }
            }
        });


        checkAppSetBtn();

        checkAgainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                checkAppSetBtn();
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        saveImage(selectedImage).execute();

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage() + "");
                    }
                }
        }
    }

    private AsyncTask<Void, Void, Boolean> saveImage(final Uri selectedImage){
        return new AsyncTask<Void, Void, Boolean>(){
            FileOutputStream fos = null;
            InputStream imageStream = null;
            File image = null;
            private ProgressDialog pDialog;

            @Override
            protected void onPreExecute(){
                try {
                    pDialog = new ProgressDialog(SettingsActivity.this);
                    pDialog.setMessage("Preparing ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    image = new File(getFilesDir(), "bckgrnd.png");
                    fos = new FileOutputStream(image);
                    imageStream = getContentResolver().openInputStream
                            (selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Boolean doInBackground(Void... params){
                if (fos != null && imageStream != null && image != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    LockScreenService.getPreferencesService().setBckgrndSet(image.getPath());
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean){
                if (!aBoolean) {
                    LockScreenService.getPreferencesService().setBckgrndSet("");
                }
                if (pDialog != null && pDialog.isShowing()) {
                    try {
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void checkAppSetBtn(){
        if (TextUtils.isEmpty(LockScreenService.getPreferencesService().getReal()) ||
                TextUtils.isEmpty(LockScreenService.getPreferencesService().getFake())) {
            btnAppSet.setEnabled(false);
        } else {
            btnAppSet.setEnabled(true);

        }
    }
}
