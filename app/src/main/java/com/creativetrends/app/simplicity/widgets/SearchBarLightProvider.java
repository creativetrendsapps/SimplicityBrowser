package com.creativetrends.app.simplicity.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.simplicity.app.R;

public class SearchBarLightProvider extends AppWidgetProvider {

    final String UPDATE_WIDGET = "updateWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.search_widget_light);

            Intent profile = new Intent(context, MainActivity.class);
            profile.putExtra("start_widget", "widget");
            profile.setAction("widget");
            PendingIntent profileIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, profile, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.simple_bar_background, profileIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(UPDATE_WIDGET.equals(intent.getAction())){
            updateWidget(context);
        }else {
            super.onReceive(context, intent);
        }

    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
    }


}
