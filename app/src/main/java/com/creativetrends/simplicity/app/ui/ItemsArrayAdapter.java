package com.creativetrends.simplicity.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creativetrends.simplicity.app.R;

import java.text.DateFormat;
import java.util.ArrayList;

import nl.matshofman.saxrssreader.RssItem;

public class ItemsArrayAdapter extends ArrayAdapter<RssItem> {

    public ItemsArrayAdapter(Context context, ArrayList<RssItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RssItem item = getItem(position);


        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView id = (TextView) convertView.findViewById(R.id.id);

        if (item.getTitle() != null)
            title.setText(item.getTitle());

        try {
            date.setText(DateFormat.getDateTimeInstance().format(item.getPubDate()));
        } catch (Exception ex) {  // NullPointerException but let's do an ugly thing just in case
            date.setText(getContext().getString(R.string.date_null));
        }

        @SuppressLint("DefaultLocale") String formattedId = "#" + String.format("%02d", position + 1);
        id.setText(formattedId);

        return convertView;
    }

}