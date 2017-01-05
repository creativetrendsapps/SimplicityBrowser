package com.creativetrends.simplicity.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.activities.MainActivity;
import com.creativetrends.simplicity.app.activities.NewWindow;

import java.util.ArrayList;

/** Created by Creative Trends Apps on 10/19/2016.*/

public class SimplicityAdapter extends Adapter<SimplicityAdapter.ViewHolderBookmark> {
    @SuppressLint("StaticFieldLeak")
    private static SimplicityAdapter adapter;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Bookmark> listBookmarks = new ArrayList<>();
    private onBookmarkSelected onBookmarkSelected;

    class ViewHolderBookmark extends ViewHolder implements View.OnClickListener {
        private Bookmark bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete;
        private TextView title;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.bookmark_title);
            delete = (ImageView) itemView.findViewById(R.id.bookmark_delete);
            bookmarkHolder = (RelativeLayout) itemView.findViewById(R.id.bookmark_holder);
        }

        void bind(Bookmark bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            bookmarkHolder.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bookmark_holder:
                    onBookmarkSelected.loadBookmark(bookmark.getTitle(), bookmark.getUrl());
                    MainActivity.bookmarksDrawer.closeDrawers();
                    NewWindow.bookmarksDrawer.closeDrawers();
                    break;
                case R.id.bookmark_delete:


                    AlertDialog.Builder removeFavorite = new AlertDialog.Builder(context);
                        removeFavorite.setTitle("Remove Favorite");
                    removeFavorite.setMessage("Are you sure you would like to remove " + bookmark.getTitle() + " from your bookmarks? This action cannot be undone.");


                    removeFavorite.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listBookmarks.remove(bookmark);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, context.getResources().getString(R.string.removed )+ " " + bookmark.getTitle() + " " + context.getResources().getString(R.string.from_bookmarks), Toast.LENGTH_LONG).show();

                        }
                    });
                    removeFavorite.setNegativeButton(R.string.cancel, null);
                    removeFavorite.show();

                    break;
                default:
                    break;
            }
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);
    }

    public SimplicityAdapter(Context context, ArrayList<Bookmark> listBookmarks, onBookmarkSelected onBookmarkSelected) {
        this.context = context;
        this.listBookmarks = listBookmarks;
        this.onBookmarkSelected = onBookmarkSelected;
        layoutInflater = LayoutInflater.from(context);
        adapter = this;
    }

    public void addItem(Bookmark bookmark) {
        listBookmarks.add(bookmark);
        notifyDataSetChanged();
    }

    public ArrayList<Bookmark> getListBookmarks() {
        return listBookmarks;

    }

    public ViewHolderBookmark onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBookmark(layoutInflater.inflate(R.layout.simplicity_marks, parent, false));
    }

    public void onBindViewHolder(ViewHolderBookmark holder, int position) {
        holder.bind(listBookmarks.get(position));
    }

    public int getItemCount() {
        return this.listBookmarks.size();

    }


}