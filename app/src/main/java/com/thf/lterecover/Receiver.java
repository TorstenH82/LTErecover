package com.thf.lterecover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.thf.lterecover.utils.Autostart;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "LTErecover";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences("default", 0);
        boolean auto = pref.getBoolean("auto", false);

        if (auto) {
            //Toast.makeText(context, "Receiver started", Toast.LENGTH_LONG).show();
            try {
                Autostart.enableAutostart(context, true);
                Log.i(TAG, "Enabled autostart");
            } catch (Autostart.SetAutostartException ex) {
                Log.e(TAG, "SetAutostartException: " + ex.getMessage());
            }
        } else {
            Log.i(TAG, "Autostart not enabled due to app settings");
            
        }
    }
}
