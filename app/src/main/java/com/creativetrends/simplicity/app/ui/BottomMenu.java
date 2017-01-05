package com.creativetrends.simplicity.app.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.creativetrends.simplicity.app.R;

import java.util.ArrayList;
import java.util.List;

/**Created by Creative Trends Apps on 1/3/2017.*/

public class BottomMenu extends FrameLayout {

    private final int BOTTOM_MENU_HEIGHT = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
    private final int ANIMATION_DURATION = 150;
    private final float MENU_ITEM_SCALE_FACTOR = 1.2f;

    private LinearLayout mItemContainer;
    private BottomMenuClickListener mBottomMenuClickListener;

    private List<BottomMenuItem> mItems;
    private int mCurrentSelectedPosition = -1;

    public BottomMenu(Context context) {
        this(context, null);
    }

    public BottomMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.mItems = mItems;
        ss.mCurrentSelectedPosition = mCurrentSelectedPosition;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mItems = ss.mItems;
        this.mCurrentSelectedPosition = ss.mCurrentSelectedPosition;
    }

    private void init(Context context) {
        mItems = new ArrayList<>();
        mItemContainer = new LinearLayout(context);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mItemContainer.setLayoutParams(params);
        mItemContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mItemContainer);
    }

    public void addItems(List<BottomMenuItem> items) {
        mItemContainer.setWeightSum(items.size());

        int count = 0;
        for (BottomMenuItem item : items) {
            item.setPosition(count);
            addItem(item);
            count++;
        }
    }

    private void addItem(BottomMenuItem item) {
        mItems.add(item);
        mItemContainer.addView(createItemView(item));
    }

    /**
     * Items should preferably be added before calling this method by using {@link #addItems(List)}
     *
     * @param listener which will be triggered on click and on addition.
     */
    public void setBottomMenuClickListener(BottomMenuClickListener listener) {
        if (mBottomMenuClickListener == null) {
            mBottomMenuClickListener = listener;

            if (mCurrentSelectedPosition > -1 && mItems.size() > mCurrentSelectedPosition) {
                // Orientation change or resume of app with already setup bottom menu.
                BottomMenuItem item = mItems.get(mCurrentSelectedPosition);
                updateItemView(mCurrentSelectedPosition, true);
                mBottomMenuClickListener.onItemSelected(mCurrentSelectedPosition, item.id, false);
            } else if (mCurrentSelectedPosition == -1 && mItems.size() > 0) {
                // First app start
                mCurrentSelectedPosition = 0;
                BottomMenuItem item = mItems.get(mCurrentSelectedPosition);
                mBottomMenuClickListener.onItemSelected(mCurrentSelectedPosition, item.id, false);
                updateItemView(mCurrentSelectedPosition, true);
            }
        }
    }

    private View createItemView(final BottomMenuItem bottomMenuItem) {
        FrameLayout parent = new FrameLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        parent.setId(bottomMenuItem.id);

        // Using larger bounds than parent but padding to shrink the icon back
        // To make the ripple effect appear outside the viewport.
        int extraPadding = dpToPx(20);
        int size = BOTTOM_MENU_HEIGHT + extraPadding;
        int padding = extraPadding;

        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);

        Drawable drawable = getResources().getDrawable(bottomMenuItem.drawableRes);
        drawable = setIconColor(drawable, bottomMenuItem.colorRes);
        imageView.setImageDrawable(drawable);
        imageView.setPadding(padding, padding, padding, padding);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setBackgroundResource(getRippleEffectResource());
        }

        parent.addView(imageView);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                updateItemView(mCurrentSelectedPosition, false);
                updateItemView(bottomMenuItem.position, true);

                if (mBottomMenuClickListener != null) {
                    if (mCurrentSelectedPosition != bottomMenuItem.position) {
                        mBottomMenuClickListener.onItemSelected(bottomMenuItem.position, bottomMenuItem.id, false);
                    } else {
                        mBottomMenuClickListener.onItemReSelected(bottomMenuItem.position, bottomMenuItem.id);
                    }
                }

                mCurrentSelectedPosition = bottomMenuItem.position;
            }
        });

        return parent;
    }

    private void updateItemView(int position, boolean selected) {
        ImageView view = (ImageView) ((FrameLayout) mItemContainer.getChildAt(position)).getChildAt(0);
        BottomMenuItem item = mItems.get(position);
        if (view != null && item != null) {
            setIconColor(view.getDrawable(), selected ? item.selectedColorRes : item.colorRes);

            view.animate()
                    .scaleX(selected ? MENU_ITEM_SCALE_FACTOR : 1f)
                    .scaleY(selected ? MENU_ITEM_SCALE_FACTOR : 1f)
                    .setDuration(ANIMATION_DURATION)
                    .start();
        }
    }

    private Drawable setIconColor(Drawable drawable, int colorRes) {
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(colorRes), PorterDuff.Mode.SRC_ATOP);
        }

        return drawable;
    }

    private int getRippleEffectResource() {
        return R.drawable.ripple;
    }

    private int dpToPx(float dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * Callback class triggered when an item is selected or re selected.
     * Re selected is when the same item, that already is selected, is clicked again.
     */
    public interface BottomMenuClickListener {
        void onItemSelected(int position, int id, boolean triggeredOnRotation);

        void onItemReSelected(int position, int id);
    }

    /**
     * Class to hold one menu item of data.
     */
    public static class BottomMenuItem implements Parcelable {
        public static final Creator<BottomMenuItem> CREATOR =
                new Creator<BottomMenuItem>() {
                    public BottomMenuItem createFromParcel(Parcel in) {
                        return new BottomMenuItem(in);
                    }

                    public BottomMenuItem[] newArray(int size) {
                        return new BottomMenuItem[size];
                    }
                };

        public int id;
        public int drawableRes;
        public int colorRes;
        public int selectedColorRes;
        public int position;

        public BottomMenuItem(int id, int drawableRes, int colorRes, int selectedColorRes) {
            this.id = id;
            this.drawableRes = drawableRes;
            this.colorRes = colorRes;
            this.selectedColorRes = selectedColorRes;
        }

        protected BottomMenuItem(Parcel in) {
            id = in.readInt();
            drawableRes = in.readInt();
            colorRes = in.readInt();
            selectedColorRes = in.readInt();
            position = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(id);
            out.writeInt(drawableRes);
            out.writeInt(colorRes);
            out.writeInt(selectedColorRes);
            out.writeInt(position);
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    /**
     * Internal helper class to keep state variables when savedInstanceState is triggered.
     */
    static class SavedState extends BaseSavedState {
        List<BottomMenuItem> mItems = new ArrayList<>();
        int mCurrentSelectedPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readTypedList(mItems, BottomMenuItem.CREATOR);
            mCurrentSelectedPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeTypedList(mItems);
            out.writeInt(mCurrentSelectedPosition);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}