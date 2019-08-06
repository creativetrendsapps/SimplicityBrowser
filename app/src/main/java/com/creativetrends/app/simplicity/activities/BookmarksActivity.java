package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.adapters.AdapterBookmarks;
import com.creativetrends.app.simplicity.adapters.BookmarkItems;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.OnStartDragListener;
import com.creativetrends.app.simplicity.utils.TabManager;
import com.creativetrends.app.simplicity.utils.TouchHelperCallback;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.app.simplicity.webview.NestedWebview;
import com.creativetrends.simplicity.app.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Creative Trends Apps.
 */

public class BookmarksActivity extends BaseActivity implements AdapterBookmarks.onBookmarkSelected, OnStartDragListener, SearchView.OnQueryTextListener {
    @SuppressLint("StaticFieldLeak")
    public static AdapterBookmarks adapterBookmarks;
    ArrayList<BookmarkItems> listBookmarks = new ArrayList<>();
    RecyclerView recyclerBookmarks;
    private ItemTouchHelper mItemTouchHelper;
    private SearchView searchView;
    SharedPreferences preferences;
    Toolbar mToolbar;
    // While the file names are the same, the references point to different files

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(UserPreferences.getBoolean("dark_mode", false)){
            setTheme(R.style.BookMarksThemeDark);
        }
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
            Cardbar.snackBar(getApplicationContext(), getString(R.string.bookmarks_hint), true).show();
            preferences.edit().putBoolean("first_bookmarks", false).apply();
        }


    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void loadBookmark(final String title, final String url) {
        int size = 0;
        if (TabManager.getList() != null) {
            size = TabManager.getList().size();
        }
        NestedWebview behe = new NestedWebview(getApplicationContext(), (MainActivity) MainActivity.getMainActivity(), ((MainActivity) MainActivity.getMainActivity()).mProgress,  ((MainActivity) MainActivity.getMainActivity()).mSearchView);
        behe.loadUrl(url);
        TabManager.addTab(behe);
        TabManager.setCurrentTab(behe);
        TabManager.updateTabView();
        ((MainActivity) MainActivity.getMainActivity()).refreshTab();
        try {
            MainActivity.mBadgeText.setText(String.valueOf(size + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(currentUser != null){
            downloadFromFirebase();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserPreferences.saveBookmarks(adapterBookmarks.getListBookmarks());
        if(currentUser != null) {
            uploadToFireBase();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //
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

    private List filter(List<BookmarkItems> bookmarks, String query) {
        query = query.toLowerCase();
        List<BookmarkItems> filteredBookmarks = new ArrayList<>();
        if (bookmarks != null && bookmarks.size() > 0) {
            for (BookmarkItems bookmark : bookmarks) {
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


    private void uploadToFireBase(){
        try{
            File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator);
            if (!bh.exists()) {
                //noinspection ResultOfMethodCallIgnored
                bh.mkdirs();
            }
            String extStorageDirectory = bh.toString();
            File file = new File(extStorageDirectory, currentUser.getUid() + ".sbh");
            ExportUtils.writeToFile(file, this);
            StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() +"/simplicity_backup/"+currentUser.getUid() + ".sbh");
            if(Uri.fromFile(file) != null){
                proimage.putFile(Uri.fromFile(file)).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return proimage.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Backed up", "sent history to Firebase");
                    } else {
                        Log.d("Failed backed up", Objects.requireNonNull(task.getException()).toString());
                    }
                });


            }

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    private void downloadFromFirebase(){
        try{
            StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() +"/simplicity_backup/"+ currentUser.getUid() + ".sbh");
            File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator, currentUser.getUid() + ".sbh");
            ExportUtils.readFromFile(bh, this);
            proimage.getFile(bh).addOnSuccessListener(taskSnapshot -> {
                Log.d("Downloaded", "got backup from Firebase");
                adapterBookmarks.notifyDataSetChanged();
            }).addOnFailureListener(exception ->
                    Log.d("Failed from Firebase", Objects.requireNonNull(exception).toString()));

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }
}
