package com.thf.lterecover;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.content.Context;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import com.thf.lterecover.utils.Autostart;
import com.thf.lterecover.utils.RestartSrv;
import com.thf.lterecover.databinding.ActivityMainBinding;
import com.thf.lterecover.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LTErecover";
    private ActivityMainBinding binding;
    private static Context context;
    private static PrefFragment settingsFragment;
    private Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences pref;
    private static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove this line if you don't want AndroidIDE to show this app's logs
        // LogSender.startLogging(this);
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        activity = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());

        context = getBaseContext();

        pref = context.getSharedPreferences("default", 0);

        settingsFragment = new PrefFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frmSettings, settingsFragment)
                .commit();

        // Receiver receiver = new Receiver();
        // registerReceiver(receiver, new IntentFilter("android.intent.action.BOOT_COMPLETED"));

        handler.post(r);

        // new Thread(r).start();
    }

    Runnable r =
            new Runnable() {
                public void run() {
                    binding.currentConnection.setText(RestartSrv.getNetworkType(context));
                    handler.postDelayed(this, 1000);
                }
            };

    public void restartEpdgWod(View view) {
        Intent intent = new Intent(this, RecoverActivity.class);
        intent.putExtra("dialog", true);
        startActivity(intent);
    }

    private boolean enableAutostart() {
        try {
            Autostart.enableAutostart(
                    context, pref.getBoolean("auto", false));
        } catch (Autostart.SetAutostartException ex) {
            Toast.makeText(
                            getBaseContext(),
                            "Error enabling autostart: " + ex.getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private String getProperty() {
        try {
            String propertyValue = Autostart.getSystemProperty(getString(R.string.propName));
            propertyValue = ("".equals(propertyValue) ? "<empty>" : propertyValue);
            return propertyValue;
        } catch (Autostart.SysPropException ex) {
            return "<can't read property>";
        }
    }

    public static class PrefFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            SwitchPreference auto = findPreference("auto");
            Preference property = findPreference("property");
            SeekBarPreference delay = findPreference("delay");
            SeekBarPreference delayBtw = findPreference("delayBtw");

            switch (key) {
                    /*
                    case "auto":
                        if (auto.isChecked()) {
                            boolean check = activity.enableAutostart();
                            property.setSummary(activity.getProperty());
                            if (!check) {
                                auto.setChecked(false);
                            }
                        }

                        break;
                    */
                case "delay":
                    delay.setSummary(delay.getValue() + "");
                    int progress = (Math.round(delay.getValue() / 5)) * 5;
                    delay.setValue(progress);
                    break;
                case "delayBtw":
                    delayBtw.setSummary(delayBtw.getValue() + "");
                    // int progress = (Math.round(delayBtw.getValue() / 5)) * 5;
                    // delayBtw.setValue(progress);
                    break;
                case "auto":
                    if (auto.isChecked()) {
                        boolean check = activity.enableAutostart();
                        if (!check) {
                            auto.setChecked(false);
                        }
                    }
            }

            property.setSummary(activity.getProperty());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String str) {
            getPreferenceManager().setSharedPreferencesName("default");
            setPreferencesFromResource(R.xml.preferences, str);

            SwitchPreference auto = findPreference("auto");
            if (Utils.isPackageInstalled(context, getString(R.string.AppSwitcherPackage))) {
                auto.setChecked(false);
                auto.setEnabled(false);
                auto.setSelectable(false);
                auto.setSummary("Please enable 'Start LTErecover' in AppSwitcher settings");
            }
            
            
            SeekBarPreference delay = findPreference("delay");
            delay.setMin(0);
            delay.setUpdatesContinuously(true);
            int value = delay.getValue();
            delay.setSummary(value + "");

            SeekBarPreference delayBtw = findPreference("delayBtw");
            value = delayBtw.getValue();
            delayBtw.setUpdatesContinuously(true);
            delayBtw.setSummary(value + "");

            Preference property = findPreference("property");
            property.setSummary(activity.getProperty());

            findPreference("prefAbout")
                    .setSummary(
                            "version "
                                    + BuildConfig.VERSION_NAME
                                    + "\n\nManufacturer: "
                                    + android.os.Build.MANUFACTURER
                                    + "\nProduct: "
                                    + android.os.Build.PRODUCT
                                    + "\nDevice: "
                                    + android.os.Build.DEVICE
                                    + "\nBoard: "
                                    + android.os.Build.BOARD);
        }
    }
}
