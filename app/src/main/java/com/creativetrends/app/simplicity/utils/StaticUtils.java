package com.creativetrends.app.simplicity.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.palette.graphics.Palette;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.simplicity.app.R;
import com.google.common.net.InternetDomainName;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Creative Trends Apps.
 */

public class StaticUtils {
    private static final String ALLOWED_CHARACTERS = UUID.randomUUID().toString().replace("-", "_").replace("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "").replace("abcdefghijklmnopqrztuvwxyz", "") + "JANAETYKESHIAJORELLSHARELL"+ SimplicityApplication.getContextOfApplication().getResources().getString(R.string.app_name);


    public static int darkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.0f;
        return Color.HSVToColor(hsv);
    }

    public static int colorNav(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.0f;
        return Color.HSVToColor(hsv);
    }

    public static int lightColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= 0.6f;
        return Color.HSVToColor(hsv);
    }


    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    public static int getColor(Bitmap bitmap, boolean incognito) {
        Palette palette = Palette.from(bitmap).generate();
        final int fallback = Color.TRANSPARENT;
        return incognito ? palette.getMutedColor(fallback) : palette.getVibrantColor(fallback);
    }


    public static int adjustAlpha(int color, @SuppressWarnings("SameParameterValue") float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
    public static int parseColor (String color, @SuppressWarnings("SameParameterValue") float factor){
        int alpha = Math.round(Color.alpha(Color.parseColor(color)) * factor);
        int red = Color.red(Color.parseColor(color));
        int green = Color.green(Color.parseColor(color));
        int blue = Color.blue(Color.parseColor(color));
        return Color.argb(alpha, red, green, blue);
    }


    public static int getColor(Context context){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.iconPrefColor, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }



    public static void hideKeyboard(View view) {
        InputMethodManager imm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imm = view.getContext().getSystemService(InputMethodManager.class);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static int fetchColorPrimary(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {

            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public static int fetchColorPrimaryDark(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimaryDark;
        } else {

            colorAttr = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }


    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    // method for clearing cache
    public static void deleteCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib") && !s.equals("shared_prefs") && !s.equals("databases")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    // helper method for clearing cache
    private static boolean deleteDir(File dir) {
        if (dir == null)
            return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success)
                    return false;
            }
        }
        return dir.delete();
    }


    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        @SuppressWarnings("IntegerDivisionInFloatingPointContext")
        float radius = bitmap.getWidth() / 2;
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return Bitmap.createScaledBitmap(out, 192, 192, true);
    }


    @WorkerThread
    public static Bitmap createScaledBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap.getWidth() <= maxWidth && bitmap.getHeight() <= maxHeight) {
            return bitmap;
        }

        if (maxWidth <= 0 || maxHeight <= 0) {
            return bitmap;
        }

        int newWidth  = maxWidth;
        int newHeight = maxHeight;

        float widthRatio  = bitmap.getWidth()  / (float) maxWidth;
        float heightRatio = bitmap.getHeight() / (float) maxHeight;

        if (widthRatio > heightRatio) {
            newHeight = (int) (bitmap.getHeight() / widthRatio);
        } else {
            newWidth = (int) (bitmap.getWidth() / heightRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public static Bitmap getCroppedBitmap(Bitmap circle) {
        final Bitmap bitmap = Bitmap.createBitmap(circle.getWidth(), circle.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, circle.getWidth(), circle.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(circle.getWidth() >> 1, circle.getHeight() >> 1, circle.getWidth() >> 1, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(circle, rect, rect, paint);
        return bitmap;
    }


    @SuppressWarnings (value="unused")
    public static boolean appInstalledOrNot(Context content, String uri) {
        PackageManager pm = content.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }



    public static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;   // add LIGHT_STATUS_BAR to flag
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags &= ~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // use XOR here for remove LIGHT_STATUS_BAR from flags_settings
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static void setLightNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;   // add LIGHT_STATUS_BAR to flag
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static void clearNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags &= ~ View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; // use XOR here for remove LIGHT_STATUS_BAR from flags_settings
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static void copyTextToClipboard(Context context, View view, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Cardbar.snackBar(context, "Copied to clipboard", true).show();

    }

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for(int i=0;i <sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getDomainName(@NonNull String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        domain = domain.startsWith("www.") ? domain.substring(4) : domain;
        try {
            //Try to get the domain without subdomain. Ex: mobile.twitter.com would returns twitter.com
            //Note: InternetDomainName is from Guava library
            return InternetDomainName.from(domain).topPrivateDomain().toString();
        } catch (IllegalStateException e) {
            Log.e("getDomainName()", "Illegal url");
        }
        return domain;
    }


    public static String fixURL(String q){
        while (q.endsWith(" "))
            q= q.substring(0, q.length()-1);
        if (q.contains(".") && !q.contains(" ")){
            if (q.startsWith("http://")||q.startsWith("https://"))
                return q;
            else if (q.startsWith("www."))
                return "http://"+q;
            else if (q.startsWith("file:"))
                return q;
            else
                return "http://"+q;
        }
        else if (q.startsWith("about:")||q.startsWith("file:"))
            return q;
        else
            return UserPreferences.getString("search_engine", "")+q.replace(" ", "%20").replace("+", "%2B").replace("&","%26");
    }


    public static String getFileSize(long size){
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static void openDownloads(@NonNull Activity activity) {
        try {
            if (isSamsung()) {
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles");
                assert intent != null;
                intent.setAction("samsung.myfiles.intent.action.LAUNCH_MY_FILES");
                intent.putExtra("samsung.myfiles.intent.extra.START_PATH", getDownloadsFile().getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } else {
                Intent downloadManagerIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                downloadManagerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(downloadManagerIntent);
            }
        }catch (ActivityNotFoundException | NullPointerException z){
            z.printStackTrace();
        }
    }

    private static boolean isSamsung() {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null) return manufacturer.toLowerCase().equals("samsung");
        return false;
    }

    private static File getDownloadsFile() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public static boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

}