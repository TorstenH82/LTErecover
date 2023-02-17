package com.thf.lterecover.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.thf.lterecover.BuildConfig;
import com.thf.lterecover.R;
import com.thf.lterecover.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Autostart {
    private static final String TAG = "LTErecover";

    public static class SetAutostartException extends Exception {
        public SetAutostartException(String message) {
            super(message);
        }
    }

    public static class SysPropException extends Exception {
        public SysPropException(String message) {
            super(message);
        }
    }

    /*
    public static void setSystemProperty(Context context, boolean enable) {

        String addProp =
                "P "
                        + BuildConfig.APPLICATION_ID
                        + "#A "
                        + BuildConfig.APPLICATION_ID
                        + ".RecoverActivity"
                        + "#Z " // this is just to add a custom tag
                        + BuildConfig.APPLICATION_ID;

        String tempPath = "";
        try {
            File temp = File.createTempFile("build", ".tmp");
            tempPath = temp.getAbsolutePath();
            BufferedReader br = new BufferedReader(new FileReader("/vendor/build.prop"));
            PrintWriter pw = new PrintWriter(new FileWriter(temp));

            String newProp = "";
            boolean addedRemoved = false;

            String line;

            while ((line = br.readLine()) != null) {
                boolean printLine = true;
                if (line.contains("sys.qb.startapp_onresume")) {
                    printLine = false;
                    String value = line.split("=")[1];
                    if (value != null && value != "") {
                        String[] entries = value.split("%");
                        for (String entry : entries) {
                            if (!entry.contains("P " + BuildConfig.APPLICATION_ID)) {
                                // keep existing properties
                                newProp = newProp + "%" + entry;
                            }
                        }
                    }
                    if (enable) {
                        newProp = newProp + "%" + addProp;
                    }
                    if (!"".equals(newProp) && "%".equals(newProp.substring(0, 1))) {
                        newProp = newProp.substring(1); // remove leading %
                    }
                    pw.println("sys.qb.startapp_onresume=" + newProp);
                    addedRemoved = true;
                }
                // keep all other build.prop entries
                if (printLine) pw.println(line);
            }

            if (!addedRemoved) {
                pw.println("sys.qb.startapp_onresume=" + addProp);
                pw.println("");
            }

            br.close();
            pw.close();

        } catch (IOException ex) {
            return;
        }

        String remountCommand = "mount -o rw,remount /vendor";
        try {
            Utils.execSuCommand(context, remountCommand);
        } catch (Utils.SuCommandException ignore) {
            return;
        }

        String command = "";
        try {
            command = "remount /vendor";
            Utils.execSuCommand(context, command);

            command = String.format("cp %s %s", tempPath, "/vendor/build.prop");
            Utils.execSuCommand(context, command);

            command = String.format("chmod 644 %s", "/vendor/build.prop");
            Utils.execSuCommand(context, command);

        } catch (Utils.SuCommandException ex) {
        }
    }
    */
    
    public static void enableAutostart(Context context, boolean enable)
            throws SetAutostartException {
        try {
            boolean added = false;
            String addProp =
                    "P "
                            + BuildConfig.APPLICATION_ID
                            + "#A "
                            + BuildConfig.APPLICATION_ID
                            + ".RecoverActivity";

            String prop = getSystemProperty("sys.qb.startapp_onresume");
            if (prop == null) prop = "";

            if (!enable && !prop.contains(BuildConfig.APPLICATION_ID)) return;

            if (enable && addProp.equals(prop)) return;

            String newProp = "";
            if (enable) newProp = addProp;

            Utils.execSuCommand(
                    context,
                    String.format("setprop %s \"%s\"", "sys.qb.startapp_onresume", newProp));
        } catch (SysPropException e) {
            throw new SetAutostartException("SysPropException: " + e.getMessage());
        } catch (Utils.SuCommandException e) {
            throw new SetAutostartException("SuCommandException: " + e.getMessage());
        }
    }
    

     

    public static String getSystemProperty(String key) throws SysPropException {
        String value = null;
        try {
            value =
                    (String)
                            Class.forName("android.os.SystemProperties")
                                    .getMethod("get", String.class)
                                    .invoke(null, key);
        } catch (Exception e) {
            throw new SysPropException(
                    "Error getting system property \"" + key + "\": " + e.getMessage());
        }
        return value;
    }
}
