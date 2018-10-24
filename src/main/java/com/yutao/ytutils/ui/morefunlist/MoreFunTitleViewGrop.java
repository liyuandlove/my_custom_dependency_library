package com.yutao.ytutils.ui.morefunlist;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/18 9:21
 * d：
 */
public class MoreFunTitleViewGrop extends RelativeLayout {
    private String TAG = "MoreFunViewGrop";
    private OnViewVisbilityChangeListener onViewVisbilityChangeListener;
    private int height = 0;


    public void setOnViewVisbilityChangeListener(OnViewVisbilityChangeListener onViewVisbilityChangeListener) {
        this.onViewVisbilityChangeListener = onViewVisbilityChangeListener;
    }

    public MoreFunTitleViewGrop(Context context) {
        super(context);
        initViews();
    }

    public MoreFunTitleViewGrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public MoreFunTitleViewGrop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
        setWillNotDraw(true);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (onViewVisbilityChangeListener!=null){
            onViewVisbilityChangeListener.onVisibilityChanged(this,visibility == View.VISIBLE);
        }
        Log.d("MoreFunViewGrop", "onWindowVisibilityChanged: "+(visibility == View.VISIBLE));
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("MoreFunViewGrop", "onDetachedFromWindow: ");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("MoreFunViewGrop", "onAttachedToWindow: ");
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        for (int i = 0;i<getChildCount();i++) {
            height = height+getChildAt(i).getMeasuredHeight();
        }
        Log.d(TAG, "addView: "+height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: "+w+" "+h);
        if (h!=0)
            height = h;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);//这样可以让布局在撑大后不变小
    }

    public interface  OnViewVisbilityChangeListener{
        void onVisibilityChanged(View view,boolean isVisibility);
    }
}
