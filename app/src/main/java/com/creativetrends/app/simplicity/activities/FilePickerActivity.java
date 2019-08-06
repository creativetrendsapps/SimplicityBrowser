/*
 * *
 *  * Created by Jorell Rutledge on 1/10/19 3:03 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 1/10/19 3:03 PM
 *
 */

package com.creativetrends.app.simplicity.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.adapters.AdapterDownloads;
import com.creativetrends.app.simplicity.adapters.Files;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

import java.io.File;
import java.util.ArrayList;

public class FilePickerActivity extends AppCompatActivity  {
    RecyclerView recyclerView;
    AdapterDownloads recyclerViewAdapter;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (UserPreferences.getBoolean("dark_mode", false)) {
            setTheme(R.style.SettingsThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        recyclerView = findViewById(R.id.recycler_view);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerViewAdapter = new AdapterDownloads(FilePickerActivity.this, getUserDownloads());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }




    private ArrayList<Files> getUserDownloads() {
        ArrayList<Files> filesList = new ArrayList<>();
        Files f;
        String targetPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Downloads";
        File targetDirector = new File(targetPath);
        File[] files = targetDirector.listFiles();
        try {
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    f = new Files();
                    f.setName("Unknown file: "+(i+1));
                    f.setFilename(file.getName());
                    f.setUri(Uri.fromFile(file));
                    f.setPath(files[i].getAbsolutePath());
                    filesList.add(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesList;
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}