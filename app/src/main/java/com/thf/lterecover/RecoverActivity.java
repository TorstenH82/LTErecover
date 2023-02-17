package com.thf.lterecover;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.thf.lterecover.utils.RestartSrv;
import android.widget.Toast;

public class RecoverActivity extends AppCompatActivity {
    private static final String TAG = "LTErecover";
    private static int delay = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Log.d(TAG, "Started RecoverActivity");

        Intent intent = getIntent();
        boolean dialog = intent.getBooleanExtra("dialog", false);
        boolean force = dialog;
        
        SharedPreferences pref = context.getSharedPreferences("default", 0);
        boolean auto = pref.getBoolean("auto", false);
        int delayBtw = pref.getInt("delayBtw", 0);

        delay = pref.getInt("delay", 0);
        
        
        Runnable r =
                new Runnable() {
                    public void run() {
                        try {
                            RestartSrv.restartService(context, delay, delayBtw, force);
                        } catch (RestartSrv.RestartSrvException e) {
                            //Log.e(TAG, e.getMessage());
                            if (dialog) {
                                runOnUiThread(
                                        () ->
                                                Toast.makeText(
                                                                getApplicationContext(),
                                                                "Error: " + e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show());
                            }
                        }
                    }
                };

        // 
        if (dialog) {
            delay = 0;
            }
        new Thread(r).start();
        
        finish();
    }
}
