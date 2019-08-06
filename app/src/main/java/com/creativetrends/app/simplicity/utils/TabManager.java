package com.creativetrends.app.simplicity.utils;


import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.webview.NestedWebview;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TabManager {
   private static List<NestedWebview> mViewsList = new ArrayList<>();
   private static PreferenceManager MANAGER;
   private static NestedWebview currentTab;
   private static NavigationView VIEW;
   public static void addTab(NestedWebview view){
       mViewsList.add(view);
   }
   @Nullable
    public static List<NestedWebview> getList(){
        return mViewsList;
    }

    public static void removeTab(NestedWebview view){
        int index = mViewsList.indexOf(view);
        if(index != 0){
          mViewsList.remove(view);
        }else {
            NestedWebview behe = mViewsList.get(index + 1);
            mViewsList.set(0,behe);
            mViewsList.remove(index + 1);
            mViewsList.remove(view);
            setCurrentTab(behe);
        }
        view.destroy();
    }

    public static NestedWebview getCurrentTab(){
        if(currentTab != null) {
            return currentTab;
        }
        else{
            return mViewsList.get(0);
        }
    }
    public static void setNavigationView(NavigationView view){
        VIEW = view;
    }
    public static void setCookie(boolean cookie){
        for (NestedWebview view : mViewsList){
            CookieManager.getInstance().setAcceptThirdPartyCookies(view,cookie);
        }
    }
    public static void updateTabView(){
        VIEW.getMenu().clear();
        for(int i = 0;i < mViewsList.size();i++) {
            NestedWebview view = mViewsList.get(i);
            VIEW.getMenu().add(view.getTitle());
            if(view == TabManager.currentTab){
                VIEW.getMenu().getItem(i).setChecked(true);
            }
            else{
                VIEW.getMenu().getItem(i).setChecked(false);
            }
        }

        for(int i = 0;i < VIEW.getMenu().size() ; i++){
            int col = ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.md_blue_600);
            TextDrawable drawable = TextDrawable.builder().buildRound("",col);
            VIEW.getMenu().getItem(i).setIcon(drawable);
        }
    }
    public static void setCurrentTab(NestedWebview view){
        assert getList() != null;
        for(NestedWebview behe : getList()){
            behe.setIsCurrentTab(false);
        }
        view.setIsCurrentTab(true);
        currentTab = view;
    }
    public static NestedWebview getTabByTitle(String title){
        assert getList() != null;
        for(NestedWebview view : getList()){
            String web = view.getTitle();
            if(web.matches(title)){
                return view;
            }
            else{
                return null;
            }
        }
        return null;
    }
    public static NestedWebview getTabAtPosition(MenuItem menuItem){
        List<MenuItem> items = new ArrayList<>();
        Menu menu = VIEW.getMenu();
        for(int i = 0; i < menu.size();i++){
            MenuItem item = menu.getItem(i);
            items.add(item);
        }
        int index = items.indexOf(menuItem);
        return Objects.requireNonNull(getList()).get(index);
    }
    public static void removeAllTabs(){
       mViewsList.clear();
    }
    public static void resetAll(AppCompatActivity act, AnimatedProgressBar pBar, SimpleAutoComplete txt){
          for(NestedWebview view : mViewsList){
              view.setNewParams(txt,pBar, act);
          }
    }
    public static void stopPlayback() {
        for (NestedWebview view : mViewsList) {
            view.onPause();
            view.pauseTimers();
        }
    }

    public static void resume() {
        for (NestedWebview view : mViewsList) {
            view.onResume();
            view.resumeTimers();
            //CSSInjection.injectFontChoice(SimplicityApplication.getContextOfApplication(), view);
        }
    }


}

