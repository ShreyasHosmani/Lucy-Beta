package com.element.lucy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.Files;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * This code is in no manner open for anyone to use. The use of this code
 * in any manner anywhere is governed by Shreyas Hosmani (yours truly)
 * for all eternity. Now go die.
 **/

/* This class is meant for variables, objects or methods that are common
 * to two or more classes within this package */

public class GlobalClass {

    /* The following boolean variable 'networkAvailable' can be used anywhere in the
     * package to verify internet access instantly. However, it is very likely that it
     * contains a stale value and thus needs regular updating */

    public static boolean networkAvailable = false;

    /* The following boolean variable 'rootAvailable' can be used anywhere in the package
     * to verify root access instantly. However, it is very likely that it contains a
     * stale value and thus needs regular updating */


    /* The following method will check for internet access and return a boolean value:
     * true: internet access available
     * false: internet access unavailable */

    public static final boolean isNetworkAvailable(Context context) {

        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

    }




    /* The following method will check if the app is being launched for the first time
     * since installation and return a boolean value:
     * true: app has been launched for the first time since installation
     * false: app has been launched earlier after installation and this is not the first time*/

    public static final boolean isAppLaunchFirst(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.element.onyx_preferences", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("APP_LAUNCH_FIRST", true)) return true;
        else return false;

    }

    /* The following method will set the value of 'APP_LAUNCH_FIRST' in the app's shared preferences
     * to false to ensure that the introduction screen is not displayed more than once */

    public static final void setAppLaunchFirst(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.element.onyx_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("APP_LAUNCH_FIRST", false);
        editor.commit();

    }






    /* The following method will log data and append it to a file ('error_log.log') in
     * the app's internal storage. */

    public static final void logError(String data, Context context) {

        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getDefault());
        data = "\n" + context.getResources().getString(R.string.text_line_break) + "\n" + data;
        data += "\n\n" + dateFormat.format(Calendar.getInstance().getTime()) + "\n" + context.getResources().getString(R.string.text_line_break) + "\n\n";

        try {

            if (readLog(context).contains("Nothing")) {

                Files.write(data, new File(context.getFilesDir(), "error_log.log"), StandardCharsets.UTF_8);

            } else {

                Files.append(data, new File(context.getFilesDir(), "error_log.log"), StandardCharsets.UTF_8);

            }

            System.out.println("Error logged!");

        } catch (IOException e) {

            System.out.println("Unable to log error; caused by: " + e.getStackTrace().toString());

        }

    }

    /* The following method will read error logs and return them all together as a String object*/

    public static final String readLog(Context context) {

        try {

            return Files.toString(new File(context.getFilesDir(), "error_log.log"), StandardCharsets.UTF_8);

        } catch (IOException e) {

            System.out.println("Unable to read error log; caused by: " + e.getStackTrace().toString());
            return "Nothing here. Looks like nothing ever went wrong.";

        }

    }





}