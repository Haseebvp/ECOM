package Utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.List;

import database.DBAdapter;
import models.Datum;

/**
 * Created by haseeb on 20/2/17.
 */

public class Utils {
    public static Typeface NormalCustomTypeface(Context context){
        Typeface face=Typeface.createFromAsset(context.getAssets(),
                "fonts/helvetica-normal.ttf");

        return face;

    }
    public static Typeface LightCustomTypeface(Context context){
        Typeface face=Typeface.createFromAsset(context.getAssets(),
                "fonts/helvetica-light.ttf");

        return face;

    }

    public static Typeface BoldCustomTypeface(Context context){
        Typeface face=Typeface.createFromAsset(context.getAssets(),
                "fonts/helvetica-bold.ttf");

        return face;

    }

    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static Boolean permissionCheck(Context context, String permission){
        if (ContextCompat.checkSelfPermission(context,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
//                    Manifest.permission.READ_CONTACTS)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {

                // No explanation needed, we can request the permission.

//                ActivityCompat.requestPermissions(thisActivity,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//            }
            return true;
        }
        else {
            return false;
        }
    }

    public static int getTotalPrice(Context context) {
        List<Datum> data = DBAdapter.getInstance(context).getCartList();
        int temp = 0;
        for (Datum item : data) {
            temp = temp + Integer.parseInt(item.getPrice());
        }
        return temp;
    }

}
