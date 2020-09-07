package com.creativetrends.app.simplicity.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.app.simplicity.adapters.AdapterSharer;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Bottomsheetshare extends BottomSheetDialogFragment {
    Context context;
    public static Bottomsheetshare newInstance() {
        return new Bottomsheetshare();
    }


    @Override
    public int getTheme() {
        if (UserPreferences.getBoolean("dark_mode", false)) {
            return R.style.BottomSheetDialog_Rounded;
        } else {
            return R.style.BottomSheetDialog_Rounded_Light;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_share_sheet, null);
        TextView screenshot = v.findViewById(R.id.screen_shot);
        screenshot.setOnClickListener(view -> {
            ((MainActivity) MainActivity.getMainActivity()).closeSheet();
            Toast.makeText(context, "Getting screenshot ready for Simplicity...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                ((MainActivity) MainActivity.getMainActivity()).takeScreenshot();
            }, 1000);
        });

        TextView copy = v.findViewById(R.id.copy_link);
        copy.setOnClickListener(view -> {
            ((MainActivity) MainActivity.getMainActivity()).closeSheet();
            new Handler().postDelayed(() -> ((MainActivity) MainActivity.getMainActivity()).copyLink(), 100);
        });

        TextView sync = v.findViewById(R.id.sync_account);
        sync.setOnClickListener(view -> {
            ((MainActivity) MainActivity.getMainActivity()).closeSheet();
            new Handler().postDelayed(() -> ((MainActivity) MainActivity.getMainActivity()).syncAccount(), 100);
        });

        TextView print = v.findViewById(R.id.print);
        print.setOnClickListener(view -> {
            ((MainActivity) MainActivity.getMainActivity()).closeSheet();
            new Handler().postDelayed(() -> ((MainActivity) MainActivity.getMainActivity()).printPage(), 100);
        });

        AdapterSharer adapterBookmarks = new AdapterSharer();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterBookmarks);

        dialog.setContentView(v);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) v.getParent()).getLayoutParams();
        //noinspection rawtypes

        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            //noinspection rawtypes
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                        case BottomSheetBehavior.STATE_SETTLING:
                        case BottomSheetBehavior.STATE_EXPANDED:
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            break;
                        }
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            break;
                        }
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }
}
