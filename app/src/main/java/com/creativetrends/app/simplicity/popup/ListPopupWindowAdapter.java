package com.creativetrends.app.simplicity.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.creativetrends.simplicity.app.R;

import java.util.List;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/11/2018.
 */
public class ListPopupWindowAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<Item> mItemList;

    public ListPopupWindowAdapter(Context context, List<Item> itemList) {
        mLayoutInflater = LayoutInflater.from(context);
        mItemList = itemList;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Item getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_popup_menu, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(getItem(position).getTitle());
        holder.ivImage.setImageResource(getItem(position).getImageRes());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        ImageView ivImage;

        ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.text);
            ivImage = view.findViewById(R.id.image);
        }
    }
}