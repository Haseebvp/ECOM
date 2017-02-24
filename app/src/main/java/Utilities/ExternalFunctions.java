package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

/**
 * Created by haseeb on 19/2/17.
 */

public class ExternalFunctions {
    public static String PREFS_NAME = "NEXT";
    public static int getScreenWidth(Context context){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return width;
    }

    public static void setNext(Boolean next, Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("next", next);
        // Commit the edits!
        editor.apply();
    }
    public static Boolean getNext(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean("next", false);
    }
}
