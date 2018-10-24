package com.yutao.ytutils.ui.customswiperefresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yutao.ytutils.R;


public class OnLoadView extends RelativeLayout {
    private ProgressBar onLoadProgessPB;
    private TextView onLoadHintTV;
    private View contentView;
    private int backgroundColor = Color.WHITE
            ,textColor = Color.BLACK
            ,progressBarColor = Color.parseColor("#1E90FA")
            ;

    public OnLoadView(Context context) {
        super(context);
        init();
    }

    public OnLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setBackgroundColor(int color){
        this.backgroundColor = color;
        super.setBackgroundColor(color);
    }

    public int getProgressBarColor() {
        return progressBarColor;
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
        Drawable wrapDrawable = DrawableCompat.wrap(onLoadProgessPB.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, progressBarColor);
        onLoadProgessPB.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
    }

    public void setTextColor(int color){
        this.textColor = color;
        onLoadHintTV.setTextColor(color);
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.custom_view_custom_swipe_onload_layout_onrefresh,this);
        contentView = findViewById(R.id.contentView);
        onLoadProgessPB= (ProgressBar) findViewById(R.id.onLoadProgessPB);
        onLoadHintTV= (TextView) findViewById(R.id.onLoadHintTV);
    }

    public void showUpToOnload(){
        onLoadProgessPB.setVisibility(GONE);
        onLoadHintTV.setText(getContext().getResources().getString(R.string.up_can_load_more));
    }
    public void releaseToOnload(){
        onLoadProgessPB.setVisibility(GONE);
        onLoadHintTV.setText(getContext().getResources().getString(R.string.up_to_load_more));
    }
    public void loading(){
        Drawable wrapDrawable = DrawableCompat.wrap(onLoadProgessPB.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, progressBarColor);
        onLoadProgessPB.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        onLoadProgessPB.setVisibility(VISIBLE);
        onLoadHintTV.setText(getContext().getResources().getString(R.string.loading_more));
    }

    public void endOnload(){
        onLoadProgessPB.setVisibility(GONE);
        onLoadHintTV.setText(R.string.load_finshed);
    }
}
