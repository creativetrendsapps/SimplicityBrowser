package com.creativetrends.app.simplicity.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.creativetrends.app.simplicity.helpers.Helpers;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.simplicity.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static com.creativetrends.app.simplicity.utils.StaticUtils.getFileSize;

public class SimplicityDownloader extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    private int fileLength;
    private long total = 0;
    private String filename;
    private File imageStorageDir;
    private final int id = 101;
    public SimplicityDownloader(Context context){
        this.context = context;
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        build = new NotificationCompat.Builder(context, context.getString(R.string.notification_widget_channel));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        build.setProgress(0, 0, true);
        final int dl_progress = (int) ((double)total / (double)fileLength * 100f);
        build.setContentText(" " +dl_progress + "%");
        build.setAutoCancel(true);
        build.setContentTitle(context.getString(R.string.downloading));
        build.setSmallIcon(android.R.drawable.stat_sys_download);
        build.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.stat_sys_download)).build();
        build.setWhen(System.currentTimeMillis());
        mNotifyManager.notify(id, build.build());
    }


    @Override
    protected String doInBackground(String... string) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Simplicity/Simplicity Downloads");

            if (!imageStorageDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }
            URL url = new URL(string[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return "Server returned HTTP " + connection.getResponseCode() + " "+ connection.getResponseMessage();
            }

            fileLength = connection.getContentLength();
            filename = URLUtil.guessFileName(string[0], null, Helpers.getMimeType(Uri.parse(string[0]).toString()));
            input = connection.getInputStream();
            output = new FileOutputStream(imageStorageDir+ File.separator +filename);
            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;

                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {

            }
            if (connection != null)
                connection.disconnect();
        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        final int dl_progress = (int) ((double)total / (double)fileLength * 100f);
        build.setProgress(100, values[0], false);
        build.setContentText(getFileSize(total)+ "/" +getFileSize(fileLength) +" " +dl_progress + "%");
        mNotifyManager.notify(id,  build.build());
        if(!StaticUtils.isNetworkConnected(context)){
            cancel(true);
        }
    }



    @Override
    protected void onPostExecute(String file_url) {
        if (file_url != null){
            mNotifyManager.cancel(id);
            build.setContentText(context.getResources().getString(android.R.string.httpErrorBadUrl))
                    .setContentTitle(filename)
                    .setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setTimeoutAfter((long) 10000)
                    .setProgress(0, 0, false)
                    .setSmallIcon(R.drawable.ic_error);
            Notification notification = build.build();
            mNotifyManager.notify(id, notification);
            Cardbar.snackBar(context, context.getString(R.string.download_failed, filename), true).show();
        }else {
            try {
                mNotifyManager.cancel(id);
                File newFile = new File(imageStorageDir + File.separator, filename);
                Uri files = FileProvider.getUriForFile(context, context.getString(R.string.auth), newFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(files, getMimeType(files));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                build.setContentIntent(resultPendingIntent)
                        .setShowWhen(true)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(filename)
                        .setContentText("Download complete "+ context.getString(R.string.bullet) +" "+ getFileSize(fileLength))
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_download_done))
                        .setProgress(0, 0, false)
                        .setSmallIcon(R.drawable.ic_check_download);
                Notification notification = build.build();
                mNotifyManager.notify(id, notification);
                Cardbar.snackBar(context, context.getString(R.string.download_successful, filename), true).show();
                MediaScannerConnection.scanFile(context, new String[]{imageStorageDir + File.separator + filename}, null, (path1, uri1) -> {
                });
            } catch (ActivityNotFoundException e) {
                Cardbar.snackBar(context, "Cannot open file.", true).show();
            } catch (Exception p){
                p.printStackTrace();
                Cardbar.snackBar(context, "Cannot open file.", true).show();
            }

        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        Cardbar.snackBar(context, context.getString(R.string.download_cancelled), true).show();

    }


    private String getMimeType(Uri uri) {
        String mimeType;
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}