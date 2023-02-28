package com.thf.lterecover.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RestartSrv {
    private static final String TAG = "LTErecover";

    public static class RestartSrvException extends Exception {
        public RestartSrvException(String message) {
            super(message);
        }
    }

    public static void restartService(Context context, int delay, int delayBtw, boolean force)
            throws RestartSrvException {

        try {
            if (!force) Thread.sleep(delay * 1000);

            String netwType = getNetworkType(context);
            Log.d(TAG, "Current network type: " + netwType);
            if ("4G".equals(netwType) && !force) {
                return;
            }

            Utils.execSuCommand(context, "stop vendor.ril-daemon-mtk");
            Log.d(TAG, "service 'vendor.ril-daemon-mtk' stopped");
            Utils.execSuCommand(context, "stop vendor.epdg_wod");
            Log.d(TAG, "service 'vendor.epdg_wod' stopped");

            Thread.sleep(delayBtw * 1000);

            Utils.execSuCommand(context, "start vendor.ril-daemon-mtk");
            Log.d(TAG, "service 'vendor.ril-daemon-mtk' started");
            Utils.execSuCommand(context, "start vendor.epdg_wod");
            Log.d(TAG, "service 'vendor.epdg_wod' started");

        } catch (Utils.SuCommandException e) {
            Log.e(TAG, "SuCommandException: " + e.getMessage());
            throw new RestartSrvException("SuCommandException: " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException in restartService");
            throw new RestartSrvException("InterruptedException in restartService");
        }
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mInfo = mConnectivityManager.getActiveNetworkInfo();

        // If not connected, "-" will be displayed
        if (mInfo == null || !mInfo.isConnected()) return "-";

        // If Connected to Wifi
        if (ConnectivityManager.TYPE_WIFI == mInfo.getType()) return "WIFI";

        if (ConnectivityManager.TYPE_MOBILE == mInfo.getType()) {

            switch (mInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return "2G";

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return "3G";

                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                case 19:
                    return "4G";

                case TelephonyManager.NETWORK_TYPE_NR:
                    return "5G";

                default:
                    return mInfo.getSubtype() + "";
            }
        }
        return "unknown";
    }
}
