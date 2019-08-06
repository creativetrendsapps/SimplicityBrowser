package com.creativetrends.app.simplicity.suggestions;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Creative Trends Apps.
 */

public class SuggestionsAdapter extends BaseAdapter implements Filterable {
    private final ArrayList<String> mItems = new ArrayList<>();
    private final LayoutInflater mInflater;
    private final ItemFilter mFilter;
    private String mQueryText;

    public SuggestionsAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mFilter = new ItemFilter();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_suggest_items, parent, false);
        }

        ImageView arrow = convertView.findViewById(R.id.search_arrow);

        TextView title = convertView.findViewById(R.id.suggestion_text);
        String suggestion = mItems.get(position);

        if (mQueryText != null) {
            SpannableStringBuilder spannable = new SpannableStringBuilder(suggestion);
            String lcSuggestion = suggestion.toLowerCase(Locale.getDefault());
            int queryTextPos = lcSuggestion.indexOf(mQueryText);
            while (queryTextPos >= 0) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), queryTextPos, queryTextPos + mQueryText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                queryTextPos = lcSuggestion.indexOf(mQueryText, queryTextPos + mQueryText.length());
            }
            title.setText(spannable);
        } else {
            title.setText(suggestion);
        }
        try {
            arrow.setOnClickListener(v -> new Handler().postDelayed(() -> {
                ((MainActivity) MainActivity.getMainActivity()).mSearchView.setText(suggestion);
                ((MainActivity) MainActivity.getMainActivity()).mSearchView.setSelection(suggestion.length());

            }, 200));
        } catch (Exception i) {
            i.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                return results;
            }

            SuggestionProvider provider = new SearchSuggestions();
            String query = constraint.toString().toLowerCase(Locale.getDefault()).trim();

            List<String> items = provider.fetchResults(query);
            results.count = items.size();
            results.values = items;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItems.clear();
            ClipboardManager clipboard = (ClipboardManager) SimplicityApplication.getContextOfApplication().getSystemService(Context.CLIPBOARD_SERVICE);
            String textOnClipboard = clipboard.toString();
            if(URLUtil.isValidUrl(textOnClipboard)){
                mItems.add(textOnClipboard);
            }
            if (results.values != null) {
                List<String> items = (List<String>) results.values;
                mItems.addAll(items);

            }
            mQueryText = constraint != null ? constraint.toString().toLowerCase(Locale.getDefault()).trim() : null;
            notifyDataSetChanged();
        }



    }
}