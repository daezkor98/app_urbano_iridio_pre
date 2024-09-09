package com.urbanoexpress.iridio3.pe.util.network;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

/**
 * Created by mick on 19/05/16.
 */
public class Connection {

    private static String TAG = Connection.class.getSimpleName();

    private static boolean isReachable;

    /**
     * Checking for all possible internet providers
     * **/
    public static boolean hasNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Para un mejor rendimiento este proceso se lleva a cabo dentro de un AsyncTask
     * Revise la clase @TaskPing.
     */
    @Deprecated
    public static void hasAccessToInternet(final Context mContext) {
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll()
//                    .build();
//            StrictMode.setThreadPolicy(policy);
//        }
//

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
                    int returnVal = process.waitFor();
                    isReachable = (returnVal == 0);
                    if (isReachable) {
                        Log.d(TAG, "Internet access");
                    } else {
                        Log.d(TAG, "No Internet access");
                        showNetworkSettingsActivity(mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showMessageNotConnectedToNetwork(Context context) {
        BaseModalsView.showToast(context,
                R.string.please_connect_to_internet, Toast.LENGTH_LONG);
    }

    public static void showMessageNotConnectedToNetwork(View view) {
        new ModalHelper.BottomPopup.Builder(view,
                view.getContext().getString(R.string.msg_not_connected_to_network))
                .setAction(view.getContext().getString(R.string.text_ajustes), v -> {
                    if (CommonUtils.isAndroid10()) {
                        view.getContext().startActivity(new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY));
                    } else {
                        view.getContext().startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .build().show();
    }

    public static void showMessageNotConnectedToNetwork(final Context context, View view) {
        BaseModalsView.showSnackBar(view,
                R.string.please_connect_to_internet, Snackbar.LENGTH_LONG,
                R.string.text_ajustes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(intent);
                    }
                });
    }

    public static void showNetworkSettingsActivity(final Context mContext) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.title_dialog_connect_to_internet))
                .setMessage(mContext.getString(R.string.message_dialog_connect_to_internet))
                .setPositiveButton("Red Movil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Wi-Fi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
