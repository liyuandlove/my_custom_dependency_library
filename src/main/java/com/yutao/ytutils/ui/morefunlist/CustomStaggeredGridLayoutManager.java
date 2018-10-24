package com.yutao.ytutils.ui.morefunlist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/17 16:24
 * d：
 */
public class CustomStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }


    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public void setAutoMeasureEnabled(boolean enabled) {
        super.setAutoMeasureEnabled(enabled);
    }
}
