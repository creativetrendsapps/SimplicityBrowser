package com.creativetrends.app.simplicity.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterSharer extends RecyclerView.Adapter<AdapterSharer.PackageViewHolder> {
    ArrayList<ShareItem> appn;
    Intent intent;
    Context context;
    List<ResolveInfo> packs;
    private LayoutInflater layoutInflater;

    public AdapterSharer() {
        appn = getInstalledApps();
        context = SimplicityApplication.getContextOfApplication();
        layoutInflater = LayoutInflater.from(SimplicityApplication.getContextOfApplication());
    }


    @Override
    public int getItemCount() {
        return appn.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSharer.PackageViewHolder holder, int position) {
        for (int i = 0; i < appn.size(); i++) {
            holder.appname.setText(appn.get(position).getAppname());
            holder.icon.setImageDrawable(appn.get(position).getIcon());
            holder.relativeLayout.setOnClickListener(view -> {
                ((MainActivity) MainActivity.getMainActivity()).closeSheet();
                new Handler().postDelayed(() -> {
                    if (!appn.get(position).getAppname().isEmpty() && appn.get(position).getAppname().equals("More")) {
                        String urlToShare = ((MainActivity) MainActivity.getMainActivity()).mWebView.getUrl();
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                        intent.putExtra(Intent.EXTRA_SUBJECT, ((MainActivity) MainActivity.getMainActivity()).mWebView.getTitle());
                        MainActivity.getMainActivity().startActivity(Intent.createChooser(intent, ((MainActivity) MainActivity.getMainActivity()).mWebView.getTitle()));
                    } else {
                        ResolveInfo info = packs.get(position);
                        intent.setPackage(info.activityInfo.packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.getMainActivity().startActivity(intent);
                    }
                }, 500);
            });
        }
    }

    @NonNull
    @Override
    public AdapterSharer.PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (UserPreferences.getBoolean("dark_mode", false)) {
            return new PackageViewHolder(layoutInflater.inflate(R.layout.package_items_dark, parent, false));
        } else {
            return new PackageViewHolder(layoutInflater.inflate(R.layout.package_items, parent, false));
        }
    }


    private ArrayList<ShareItem> getInstalledApps() {
        String urlToShare = ((MainActivity) MainActivity.getMainActivity()).mWebView.getUrl();
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, ((MainActivity) MainActivity.getMainActivity()).mWebView.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        ArrayList<ShareItem> res = new ArrayList<>();
        packs = MainActivity.getMainActivity().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : packs) {
            String packageName = info.activityInfo.packageName;
            ShareItem newInfo = new ShareItem();
            newInfo.appname = info.loadLabel(MainActivity.getMainActivity().getPackageManager()).toString();
            newInfo.icon = info.loadIcon(MainActivity.getMainActivity().getPackageManager());
            res.add(newInfo);
        }
        ShareItem basicInfo = new ShareItem();
        basicInfo.appname = "More";
        basicInfo.icon = ContextCompat.getDrawable(SimplicityApplication.getContextOfApplication(), R.drawable.ic_overflow_tabs);
        res.add(basicInfo);
        return res;
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView appname;
        ImageView icon;
        RelativeLayout relativeLayout;

        public PackageViewHolder(View v) {
            super(v);
            appname = v.findViewById(R.id.pack_name);
            icon = v.findViewById(R.id.pack_image);
            relativeLayout = v.findViewById(R.id.share_holder);


        }
    }


}
