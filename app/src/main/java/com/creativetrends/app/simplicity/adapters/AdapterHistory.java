package com.creativetrends.app.simplicity.adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Creative Trends Apps.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolderBookmark> {
    @SuppressLint("StaticFieldLeak")
    private static AdapterHistory adapter;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<HistoryItems> listBookmarks;
    private ArrayList <HistoryItems> filteredBookmarks;
    private AdapterHistory.onBookmarkSelected onBookmarkSelected;

    class ViewHolderBookmark extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private HistoryItems bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete;
        private TextView title, url, date;
        private LinearLayout card;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.history_title);
            url = itemView.findViewById(R.id.history_url);
            date = itemView.findViewById(R.id.history_date_time);
            delete = itemView.findViewById(R.id.history_delete);
            bookmarkHolder = itemView.findViewById(R.id.history_holder);
            card = itemView.findViewById(R.id.bookmark_card);
            card.setClipToOutline(true);
        }

        void bind(HistoryItems bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            url.setText(bookmark.getUrl());
            if(!TextUtils.isEmpty(bookmark.getDate())){
                date.setText(bookmark.getDate());
            }else{
                date.setVisibility(View.GONE);
            }
            bookmarkHolder.setOnClickListener(this);
            bookmarkHolder.setOnLongClickListener(this);
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

        public boolean onLongClick(View v) {
            bookmarkHolder.setOnClickListener(null);
            if (bookmark.getUrl() != null) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("History Item", bookmark.getUrl());
                clipboard.setPrimaryClip(clip);
                Cardbar.snackBar(context, "URL copied to clipboard", true).show();

                new Handler().postDelayed(() -> {
                    try {
                        bookmarkHolder.setOnClickListener(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1500);
            }
            return false;
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);
    }

    public AdapterHistory(Context context, ArrayList<HistoryItems> listBookmarks, AdapterHistory.onBookmarkSelected onBookmarkSelected) {
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

    public ArrayList<HistoryItems> getListBookmarks() {
        return listBookmarks;

    }

    @NonNull
    public AdapterHistory.ViewHolderBookmark onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterHistory.ViewHolderBookmark(layoutInflater.inflate(R.layout.layout_history_items, parent, false));
    }

    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolderBookmark holder, int position) {
        holder.bind(filteredBookmarks.get(position));
    }


    private void addItem(int position, HistoryItems bookmark) {
        filteredBookmarks.add(position, bookmark);
        notifyItemInserted(position);
    }


    private void moveItem(int fromPosition, int toPosition) {
        filteredBookmarks.add(toPosition, filteredBookmarks.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }


    private HistoryItems removeItem(int position) {
        HistoryItems bookmark = filteredBookmarks.remove(position);
        notifyItemRemoved(position);
        return bookmark;
    }

    public int getItemCount() {
        return filteredBookmarks.size();
    }

    public void animateTo(List<HistoryItems> bookmarks) {
        applyAndAnimateRemovals(bookmarks);
        applyAndAnimateAdditions(bookmarks);
        applyAndAnimateMovedItems(bookmarks);
    }

    private void applyAndAnimateRemovals(List<HistoryItems> newBookmarks) {
        for (int i = filteredBookmarks.size() - 1; i >= 0; i--) {
            if (!newBookmarks.contains(filteredBookmarks.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<HistoryItems> newBookmarks) {
        int count = newBookmarks.size();
        for (int i = 0; i < count; i++) {
            HistoryItems bookmark = newBookmarks.get(i);
            if (!filteredBookmarks.contains(bookmark)) {
                addItem(i, bookmark);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<HistoryItems> newBookmarks) {
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