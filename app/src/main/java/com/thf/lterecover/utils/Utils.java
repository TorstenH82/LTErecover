package com.thf.lterecover.utils;

import android.content.pm.PackageManager;
import com.thf.lterecover.R;
import android.content.Context;
import java.io.IOException;
import java.io.DataOutputStream;

public class Utils {

    public static class SuCommandException extends Exception {
        public SuCommandException(String message) {
            super(message);
        }
    }

    public static void execSuCommand(Context context, String command) throws SuCommandException {
        Boolean error = false;
        String errorMsg = "";
        command = command == null ? "" : command;
        // String[] command = new String[] { "su", "@#zxcvbnmasdfghjklqwertyuiop1234567890,." };
        String suser = context.getString(R.string.su);
        try {
            java.lang.Process su = Runtime.getRuntime().exec(suser);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            if (!"".equals(command)) {
                outputStream.writeBytes(command + "\n");
                outputStream.flush();
            }
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();

        } catch (IOException e) {
            throw new SuCommandException("IOException:" + e.getMessage());

        } catch (InterruptedException e) {
            throw new SuCommandException("InterruptedException: " + e.getMessage());
        }
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
