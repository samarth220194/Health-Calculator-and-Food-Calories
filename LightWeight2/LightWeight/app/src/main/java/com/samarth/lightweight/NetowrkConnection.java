package com.samarth.lightweight;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sam on 17-Mar-17.
 */

public class NetowrkConnection {




    public static boolean CheckConnection(Activity activity) {

        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(activeNetwork!=null) {
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            return (isWiFi && isConnected) || (isMobile && isConnected);

        }
        else return false;

    }

}
