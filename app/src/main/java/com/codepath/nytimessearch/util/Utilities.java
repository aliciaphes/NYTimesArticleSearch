package com.codepath.nytimessearch.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Owner on 22-Oct-16.
 */

public class Utilities {

    private static final String apiKey = "f18fa33160fe414cac757019984c8b81";
    private static final String searchUrl = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    public static String getSearchUrl() {
        return searchUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void hideSoftKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static ProgressDialog createLoadingDialog(Context context) {
        //todo: create 'loading' articles dialog IN UTILITIES.JAVA:
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        return pd;
    }

}
