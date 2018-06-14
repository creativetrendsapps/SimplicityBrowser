package com.creativetrends.app.simplicity.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.creativetrends.app.simplicity.adapters.AdapterHistory;
import com.creativetrends.app.simplicity.utils.History;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.hugocastelani.waterfalltoolbar.Dp;
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Creative Trends Apps.
 */

public class HistoryActivity extends AppCompatActivity implements AdapterHistory.onBookmarkSelected, SearchView.OnQueryTextListener {
    AdapterHistory adapterBookmarks;
    ArrayList<History> listHistory = new ArrayList<>();
    RecyclerView recyclerBookmarks;
    private SearchView searchView;
    WaterfallToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setSupportActionBar(findViewById(R.id.toolbar));
        mToolbar = findViewById(R.id.waterfall_toolbar);
        mToolbar.setInitialElevation(new Dp(0).toPx());
        mToolbar.setFinalElevation(new Dp(8).toPx());
        mToolbar.setScrollFinalPosition(8);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        listHistory = UserPreferences.getHistory();
        recyclerBookmarks = findViewById(R.id.recycler_history);
        mToolbar.setRecyclerView(recyclerBookmarks);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerBookmarks.setLayoutManager(mLayoutManager);
        adapterBookmarks = new AdapterHistory(this, listHistory, this);
        recyclerBookmarks.setAdapter(adapterBookmarks);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void loadBookmark(final String title, final String url) {
        Intent peekIntent = new Intent(HistoryActivity.this, MainActivity.class);
        peekIntent.setData(Uri.parse(url));
        peekIntent.putExtra("isNewTab" , false);
        startActivity(peekIntent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            UserPreferences.saveHistory(adapterBookmarks.getListBookmarks());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_b_h, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.search_history));
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
    private List filter(List<History> bookmarks, String query) {
        query = query.toLowerCase();
        List<History> filteredBookmarks = new ArrayList<>();
        if (bookmarks != null && bookmarks.size() > 0) {
            for (History bookmark : bookmarks) {
                String searchCheck = bookmark.getTitle() + " " + bookmark.getUrl();
                if (!(searchCheck.isEmpty() || !searchCheck.toLowerCase().contains(query.toLowerCase()))) {
                    filteredBookmarks.add(bookmark);
                }
            }
        }
        return filteredBookmarks;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapterBookmarks != null) {
            adapterBookmarks.animateTo(filter(listHistory, newText));
            recyclerBookmarks.scrollToPosition(0);
        }
        return true;
    }

    void deleteAlert() {
        AlertDialog.Builder removeFavorite = new AlertDialog.Builder(this);
        removeFavorite.setTitle(R.string.clear_history);
        removeFavorite.setMessage(getResources().getString(R.string.are_you_sure_history));
        removeFavorite.setPositiveButton(getResources().getString(R.string.clear_history), (dialog, which) -> adapterBookmarks.clear());
        removeFavorite.setNegativeButton(R.string.cancel, null);
        removeFavorite.show();
    }
}
