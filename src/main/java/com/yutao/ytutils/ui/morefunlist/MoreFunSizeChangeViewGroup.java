package com.yutao.ytutils.ui.morefunlist;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/18 15:44
 * d：
 */
public class MoreFunSizeChangeViewGroup extends RelativeLayout {
    private OnSizeChangeListener onSizeChangeListener;

    public MoreFunSizeChangeViewGroup(Context context) {
        super(context);
        initViews();
    }

    public MoreFunSizeChangeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public MoreFunSizeChangeViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangeListener!=null)
            onSizeChangeListener.onSizeChange(this,w,h,oldw,oldh);
    }

    public void setScale(float scale) {
        super.setScaleX(scale);
        super.setScaleY(scale);
        for (int i = 0;i<getChildCount();i++){
            getChildAt(i).setScaleX(scale);
            getChildAt(i).setScaleY(scale);
        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public interface OnSizeChangeListener{
        void onSizeChange(MoreFunSizeChangeViewGroup viewGroup,int w, int h, int oldw, int oldh);
    }
}
