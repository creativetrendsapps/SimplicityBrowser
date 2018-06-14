package com.creativetrends.app.simplicity.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Repurposed from: https://github.com/aurelhubert/ahbottomnavigation
 */
public abstract class VerticalScrollingBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

	private int mTotalDyUnconsumed = 0;
	private int mTotalDy = 0;
	@ScrollDirection
	private int mOverScrollDirection = ScrollDirection.SCROLL_NONE;
	@ScrollDirection
	private int mScrollDirection = ScrollDirection.SCROLL_NONE;

	VerticalScrollingBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	VerticalScrollingBehavior() {
		super();
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ScrollDirection.SCROLL_DIRECTION_UP, ScrollDirection.SCROLL_DIRECTION_DOWN})
	public @interface ScrollDirection {
		int SCROLL_DIRECTION_UP = 6;
		int SCROLL_DIRECTION_DOWN = -10;
		int SCROLL_NONE = 0;
	}


	/*
	   @return Overscroll direction: SCROLL_DIRECTION_UP, CROLL_DIRECTION_DOWN, SCROLL_NONE
   */
	@SuppressLint("WrongConstant")
	@ScrollDirection
	public int getOverScrollDirection() {
		return mOverScrollDirection;
	}


	/**
	 * @return Scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE
	 */

	@SuppressLint("WrongConstant")
	@ScrollDirection
	public int getScrollDirection() {
		return mScrollDirection;
	}


	public abstract void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll);

	public abstract void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection);

	@Override
	public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes) {
		return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
	}

	@Override
	public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes) {
		super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
	}

	@Override
	public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target) {
		super.onStopNestedScroll(coordinatorLayout, child, target);
	}

	@SuppressLint("WrongConstant")
	@Override
	public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
		if (dyUnconsumed > 0 && mTotalDyUnconsumed < 4) {
			mTotalDyUnconsumed = 0;
			mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
		} else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
			mTotalDyUnconsumed = 0;
			mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
		}
		mTotalDyUnconsumed += dyUnconsumed;
		onNestedVerticalOverScroll(coordinatorLayout, child, mOverScrollDirection, dyConsumed, mTotalDyUnconsumed);
	}

	@SuppressLint("WrongConstant")
	@Override
	public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed) {
		super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
		if (dy > 0 && mTotalDy < 4) {
			mTotalDy = 0;
			mScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
		} else if (dy < 0 && mTotalDy > 0) {
			mTotalDy = 0;
			mScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
		}
		mTotalDy += dy;
		onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, mScrollDirection);
	}


	@SuppressLint("WrongConstant")
	@Override
	public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
		super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
		mScrollDirection = velocityY > 0 ? ScrollDirection.SCROLL_DIRECTION_UP : ScrollDirection.SCROLL_DIRECTION_DOWN;
		return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, mScrollDirection);
	}

	protected abstract boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection);

	@Override
	public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY) {
		return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
	}

	@NonNull
	@Override
	public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, V child, WindowInsetsCompat insets) {

		return super.onApplyWindowInsets(coordinatorLayout, child, insets);
	}

	@Override
	public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
		return super.onSaveInstanceState(parent, child);
	}

}