package com.creativetrends.app.simplicity.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creativetrends.app.simplicity.ui.CustomShadow;
import com.creativetrends.app.simplicity.utils.History;
import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Creative Trends Apps.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolderBookmark> {
    @SuppressLint("StaticFieldLeak")
    private static AdapterHistory adapter;
    Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<History> listBookmarks;
    private ArrayList <History> filteredBookmarks;
    private AdapterHistory.onBookmarkSelected onBookmarkSelected;

    class ViewHolderBookmark extends RecyclerView.ViewHolder implements View.OnClickListener {
        private History bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete, icon;
        private TextView title, url;
        private LinearLayout card;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.history_title);
            url = itemView.findViewById(R.id.history_url);
            delete = itemView.findViewById(R.id.history_delete);
            icon = itemView.findViewById(R.id.imageHistory);
            bookmarkHolder = itemView.findViewById(R.id.history_holder);
            card = itemView.findViewById(R.id.bookmark_card);
            card.setOutlineProvider(new CustomShadow(2));
            card.setClipToOutline(true);
        }

        void bind(History bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            url.setText(bookmark.getUrl());
            bookmarkHolder.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.history_holder:
                    onBookmarkSelected.loadBookmark(bookmark.getTitle(), bookmark.getUrl());
                    break;
                case R.id.history_delete:
                    listBookmarks.remove(bookmark);
                    filteredBookmarks.remove(bookmark);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);
    }

    public AdapterHistory(Context context, ArrayList<History> listBookmarks, AdapterHistory.onBookmarkSelected onBookmarkSelected) {
        this.context = context;
        this.listBookmarks = listBookmarks;
        this.onBookmarkSelected = onBookmarkSelected;
        layoutInflater = LayoutInflater.from(context);
        adapter = this;
        filteredBookmarks = new ArrayList<>(listBookmarks);
    }

    public void clear() {
        int size = listBookmarks.size();
        listBookmarks.clear();
        filteredBookmarks.clear();
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    public ArrayList<History> getListBookmarks() {
        return listBookmarks;

    }

    @NonNull
    public AdapterHistory.ViewHolderBookmark onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterHistory.ViewHolderBookmark(layoutInflater.inflate(R.layout.history_items, parent, false));
    }

    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolderBookmark holder, int position) {
        holder.bind(filteredBookmarks.get(position));
    }


    private void addItem(int position, History bookmark) {
        filteredBookmarks.add(position, bookmark);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        filteredBookmarks.add(toPosition, filteredBookmarks.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }


    private History removeItem(int position) {
        History bookmark = filteredBookmarks.remove(position);
        notifyItemRemoved(position);
        return bookmark;
    }

    public int getItemCount() {
        return filteredBookmarks.size();
    }

    public void animateTo(List<History> bookmarks) {
        applyAndAnimateRemovals(bookmarks);
        applyAndAnimateAdditions(bookmarks);
        applyAndAnimateMovedItems(bookmarks);
    }

    private void applyAndAnimateRemovals(List<History> newBookmarks) {
        for (int i = filteredBookmarks.size() - 1; i >= 0; i--) {
            if (!newBookmarks.contains(filteredBookmarks.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<History> newBookmarks) {
        int count = newBookmarks.size();
        for (int i = 0; i < count; i++) {
            History bookmark = newBookmarks.get(i);
            if (!filteredBookmarks.contains(bookmark)) {
                addItem(i, bookmark);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<History> newBookmarks) {
        int toPosition = newBookmarks.size() - 1;
        while (toPosition >= 0) {
            int fromPosition = filteredBookmarks.indexOf(newBookmarks.get(toPosition));
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
            toPosition--;
        }
    }



}