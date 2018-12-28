package com.creativetrends.app.simplicity.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.creativetrends.app.simplicity.adapters.AdapterBookmarks;
import com.creativetrends.app.simplicity.adapters.Bookmark;
import com.creativetrends.app.simplicity.utils.OnStartDragListener;
import com.creativetrends.app.simplicity.utils.TouchHelperCallback;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Creative Trends Apps.
 */

public class BookmarksActivity extends AppCompatActivity implements AdapterBookmarks.onBookmarkSelected, OnStartDragListener, SearchView.OnQueryTextListener {
    AdapterBookmarks adapterBookmarks;
    ArrayList<Bookmark> listBookmarks = new ArrayList<>();
    RecyclerView recyclerBookmarks;
    private ItemTouchHelper mItemTouchHelper;
    private SearchView searchView;
    SharedPreferences preferences;
    Toolbar mToolbar;
    // While the file names are the same, the references point to different files

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Drawable drawable = mToolbar.getNavigationIcon();
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.grey_color), PorterDuff.Mode.SRC_ATOP);
            }
        }
        listBookmarks = UserPreferences.getBookmarks();
        recyclerBookmarks = findViewById(R.id.recycler_bookmarks);
        recyclerBookmarks.setLayoutManager(new LinearLayoutManager(this));
        adapterBookmarks = new AdapterBookmarks(this, listBookmarks, this, this);
        recyclerBookmarks.setAdapter(adapterBookmarks);
        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapterBookmarks);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerBookmarks);

        //show sort hint
        if (preferences.getBoolean("first_bookmarks", true) && !listBookmarks.isEmpty()) {
            Snackbar.make(recyclerBookmarks, R.string.bookmarks_hint, Snackbar.LENGTH_SHORT).show();
            preferences.edit().putBoolean("first_bookmarks", false).apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void loadBookmark(final String title, final String url) {
        Intent peekIntent = new Intent(BookmarksActivity.this, MainActivity.class);
        peekIntent.setData(Uri.parse(url));
        peekIntent.putExtra("isNewTab" , false);
        startActivity(peekIntent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            UserPreferences.saveBookmarks(adapterBookmarks.getListBookmarks());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_b_h, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.search_bookmarks));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.clear_b_h:
                deleteAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (searchView.isIconified()) {
            super.onBackPressed();
        } else {
            searchView.setIconified(true);
        }

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapterBookmarks != null) {
            adapterBookmarks.animateTo(filter(listBookmarks, newText));
            recyclerBookmarks.scrollToPosition(0);
        }
        return true;
    }

    private List filter(List<Bookmark> bookmarks, String query) {
        query = query.toLowerCase();
        List<Bookmark> filteredBookmarks = new ArrayList<>();
        if (bookmarks != null && bookmarks.size() > 0) {
            for (Bookmark bookmark : bookmarks) {
                String searchCheck = bookmark.getTitle() + " ";
                if (!(searchCheck.isEmpty() || !searchCheck.toLowerCase().contains(query.toLowerCase()))) {
                    filteredBookmarks.add(bookmark);
                }
            }
        }
        return filteredBookmarks;
    }


    void deleteAlert() {
        AlertDialog.Builder removeFavorite = new AlertDialog.Builder(this);
        removeFavorite.setTitle(R.string.clear_bookmarks);
        removeFavorite.setMessage(getResources().getString(R.string.are_you_sure_bookmarks));
        removeFavorite.setPositiveButton(getResources().getString(R.string.clear_bookmarks), (dialog, which) -> adapterBookmarks.clear());
        removeFavorite.setNegativeButton(R.string.cancel, null);
        removeFavorite.show();
    }




}
