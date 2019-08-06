package com.creativetrends.app.simplicity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerBaseAdapter <VH extends RecyclerView.ViewHolder> extends BaseAdapter implements Filterable {

    private final RecyclerView.Adapter<VH> mAdapter;
    private final ArrayList<String> mItems = new ArrayList<>();
    private final LayoutInflater mInflater;
    private String mQueryText;

    public RecyclerBaseAdapter(RecyclerView.Adapter<VH> adapter, Context context) {
        mAdapter = adapter;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        // not supported
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if (convertView == null) {
            holder = mAdapter.createViewHolder(parent, getItemViewType(position));
            convertView = holder.itemView;
            convertView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        mAdapter.bindViewHolder(holder, position);
        return holder.itemView;
    }

    @Override
    public Filter getFilter() {
        // TODO: return a real filter
        return null;
    }
}