package com.thf.lterecover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.thf.lterecover.utils.RestartSrv;
import android.widget.Toast;

public class RecoverActivity extends Activity {
    private static final String TAG = "LTErecover";
    private Context context;
    private int delay = 0;
    private int delayBtw = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean dialog = false;
    private boolean force = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d(TAG, "Started RecoverActivity");

        Intent intent = getIntent();
        dialog = intent.getBooleanExtra("dialog", false);
        force = dialog;
    }

    private class RunnableRestartSrv implements Runnable {
        @Override
        public void run() {
            try {
                RestartSrv.restartService(context, delay, delayBtw, force);
            } catch (RestartSrv.RestartSrvException e) {
                Log.e(TAG, "Error during service restart: " + e.getMessage());
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
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "Resumed RecoverActivity");

        SharedPreferences pref = context.getSharedPreferences("default", 0);
        delayBtw = pref.getInt("delayBtw", 0);

        if (!dialog) {
            delay = pref.getInt("delay", 0);
        } else {
            delay = 0;
        }

        RunnableRestartSrv runnableRestartSrv = new RunnableRestartSrv();
        new Thread(runnableRestartSrv).start();
        
        finish();
    }
}
