package com.creativetrends.app.simplicity.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creativetrends.app.simplicity.ui.CustomShadow;
import com.creativetrends.app.simplicity.utils.OnStartDragListener;
import com.creativetrends.app.simplicity.utils.TouchHelperAdapter;
import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Creative Trends Apps.
 */

public class AdapterBookmarks extends RecyclerView.Adapter<AdapterBookmarks.ViewHolderBookmark> implements TouchHelperAdapter {
    @SuppressLint("StaticFieldLeak")
    private static AdapterBookmarks adapter;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Bookmark> listBookmarks;
    private ArrayList <Bookmark> filteredBookmarks;
    private onBookmarkSelected onBookmarkSelected;

    private final OnStartDragListener mDragStartListener;



    class ViewHolderBookmark extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText et;
        private Bookmark bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete;
        private TextView title, url, letter;
        private LinearLayout card;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookmark_title);
            url = itemView.findViewById(R.id.bookmark_url);
            delete = itemView.findViewById(R.id.bookmark_delete);
            letter = itemView.findViewById(R.id.bookmark_letter);
            bookmarkHolder = itemView.findViewById(R.id.bookmark_holder);
            card = itemView.findViewById(R.id.bookmark_card);
            card.setOutlineProvider(new CustomShadow(2));
            card.setClipToOutline(true);

        }

        void bind(Bookmark bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            url.setText(bookmark.getUrl());
            if(bookmark.getLetter()!=null && !bookmark.getLetter().isEmpty()) {
                letter.setText(bookmark.getLetter());
                letter.setBackgroundColor(bookmark.getImage());
            }else{
                String part1 = bookmark.getTitle();
                String bt = part1.substring(0,1);
                letter.setText(bt);
                letter.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
            bookmarkHolder.setOnClickListener(this);
            delete.setOnClickListener(this);
            et = new EditText(context);
        }


        private void deleteAlert() {
            AlertDialog.Builder removeFavorite = new AlertDialog.Builder(context);
            removeFavorite.setTitle(R.string.remove_bookmark);
            removeFavorite.setMessage(context.getResources().getString(R.string.are_you_sure) + " " + bookmark.getTitle() + " " + context.getResources().getString(R.string.from_bookmark));
            removeFavorite.setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                listBookmarks.remove(bookmark);
                filteredBookmarks.remove(bookmark);
                adapter.notifyDataSetChanged();
            });
            removeFavorite.setNegativeButton(R.string.cancel, null);
            removeFavorite.show();
        }


        @SuppressLint("RestrictedApi")
        private void editAlert() {
            try {
                et = new EditText(context);
                AlertDialog.Builder createFile = new AlertDialog.Builder(context);
                createFile.setCancelable(false);
                createFile.setTitle(context.getResources().getString(R.string.rename_titile));
                createFile.setMessage(context.getResources().getString(R.string.rename_message));
                //noinspection deprecation
                createFile.setView(et, 30, 5, 30, 5);
                et.setHint(bookmark.getTitle());
                createFile.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                    bookmark.setTitle(et.getText().toString());
                    String part1 = et.getText().toString();
                    String bt = part1.substring(0,1);
                    bookmark.setLetter(bt);
                    adapter.notifyDataSetChanged();
                });
                createFile.setNegativeButton(R.string.cancel, null);
                createFile.show();
            } catch (Exception ignored) {

            }

        }
        // Display anchored popup menu based on view selected
        private void showFilterPopup(View v) {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteAlert();
                        return true;
                    case R.id.menu_rename:
                        editAlert();
                        return true;
                    case R.id.menu_share:
                        try {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, bookmark.getUrl());
                            context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_bookmark)));
                        }catch (ActivityNotFoundException ignored){                            
                        }catch (Exception i){
                            i.printStackTrace();
                        }
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bookmark_holder:
                    onBookmarkSelected.loadBookmark(bookmark.getTitle(), bookmark.getUrl());
                    break;
                case R.id.bookmark_delete:
                    showFilterPopup(v);
                    break;
                default:
                    break;
            }
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);
    }

    public AdapterBookmarks(Context context, ArrayList<Bookmark> listBookmarks, onBookmarkSelected onBookmarkSelected, OnStartDragListener dragStartListener) {
        this.context = context;
        this.listBookmarks = listBookmarks;
        this.onBookmarkSelected = onBookmarkSelected;
        layoutInflater = LayoutInflater.from(context);
        adapter = this;
        mDragStartListener = dragStartListener;
        filteredBookmarks = new ArrayList<>(listBookmarks);
    }


    public ArrayList<Bookmark> getListBookmarks() {
        return listBookmarks;

    }

    @NonNull
    public ViewHolderBookmark onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderBookmark(layoutInflater.inflate(R.layout.bookmark_items, parent, false));
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(@NonNull final ViewHolderBookmark holder, int position) {
        holder.bind(filteredBookmarks.get(position));
        holder.letter.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder);
            }
            return false;
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(listBookmarks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    public void clear() {
        int size = listBookmarks.size();
        listBookmarks.clear();
        filteredBookmarks.clear();
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    private void addItem(int position, Bookmark bookmark) {
        filteredBookmarks.add(position, bookmark);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        filteredBookmarks.add(toPosition, filteredBookmarks.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }


    private Bookmark removeItem(int position) {
        Bookmark bookmark = filteredBookmarks.remove(position);
        notifyItemRemoved(position);
        return bookmark;
    }

    public int getItemCount() {
        return filteredBookmarks.size();
    }

    public void animateTo(List<Bookmark> bookmarks) {
        applyAndAnimateRemovals(bookmarks);
        applyAndAnimateAdditions(bookmarks);
        applyAndAnimateMovedItems(bookmarks);
    }

    private void applyAndAnimateRemovals(List<Bookmark> newBookmarks) {
        for (int i = filteredBookmarks.size() - 1; i >= 0; i--) {
            if (!newBookmarks.contains(filteredBookmarks.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Bookmark> newBookmarks) {
        int count = newBookmarks.size();
        for (int i = 0; i < count; i++) {
            Bookmark bookmark = newBookmarks.get(i);
            if (!filteredBookmarks.contains(bookmark)) {
                addItem(i, bookmark);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Bookmark> newBookmarks) {
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
