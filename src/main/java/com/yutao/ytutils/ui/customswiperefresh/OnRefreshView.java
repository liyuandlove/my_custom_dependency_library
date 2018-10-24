package com.yutao.ytutils.ui.customswiperefresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;

import java.util.Calendar;

public class OnRefreshView extends RelativeLayout {
    private String TAG=OnRefreshView.class.getSimpleName();

    private TextView onRefreshTag
            ,onRefreshHintTV
            ,onRefreshLastTimeTV;
    private View contentView;
    private ProgressBar onRefreshIngPB;
    private int screenWidth
            ,screenHeight
            ;
    private long lastRefreshTime;
    private SharedPreferences sharedPreferences;
    private String flag;
    private int backgroundColor = Color.WHITE
            ,textColor = Color.BLACK
            ,progressBarColor = Color.parseColor("#1E90FA")
            ;

    public OnRefreshView(Context context) {
        super(context);
        init();
    }

    public OnRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getProgressBarColor() {
        return progressBarColor;
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
        Drawable wrapDrawable = DrawableCompat.wrap(onRefreshIngPB.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, progressBarColor);
        onRefreshIngPB.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
    }

    public void setBackgroundColor(int color){
        this.backgroundColor=color;
        super.setBackgroundColor(backgroundColor);
    }

    public void setTextColor(int color){
        this.textColor=color;
        onRefreshTag.setTextColor(color);
        onRefreshHintTV.setTextColor(color);
        onRefreshLastTimeTV.setTextColor(color);
    }

    private void init(){
        Log.d(TAG, "init: "+getContext().getClass().getSimpleName());
        flag=getContext().getClass().getSimpleName();
        LayoutInflater.from(getContext()).inflate(R.layout.custom_view_custom_swipe_refresh_layout_onrefresh,this);
        screenWidth=getContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight=getContext().getResources().getDisplayMetrics().heightPixels;
        sharedPreferences=getContext().getSharedPreferences("OnRefreshView", Context.MODE_PRIVATE);

        findViews();
    }

    private void findViews(){
        contentView = findViewById(R.id.contentView);
        onRefreshTag = (TextView) findViewById(R.id.onRefreshTag);
        onRefreshHintTV = (TextView) findViewById(R.id.onRefreshHintTV);
        onRefreshLastTimeTV = (TextView) findViewById(R.id.onRefreshLastTimeTV);
        onRefreshIngPB = (ProgressBar) findViewById(R.id.onRefreshIngPB);
    }

    /**
     * 设置高度
     * @param height
     */
    public void setHeight(int height){
        if (getLayoutParams()==null)
            setLayoutParams(new MarginLayoutParams(screenWidth,0));
        MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.height=height;
        setLayoutParams(layoutParams);
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    private long getLastRefreshTimeLong(){
        if (sharedPreferences!=null){
            lastRefreshTime = sharedPreferences.getLong(flag,0);
        }
        return lastRefreshTime;
    }
    private void saveLastRefreshTime(long lastTime){
        if (sharedPreferences==null)
            return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(flag,lastTime);
        editor.apply();
    }

    public String getLastRefreshTime(){
        long lastTime=getLastRefreshTimeLong();
        if (lastTime==0){
            return getContext().getResources().getString(R.string.never_refresh);
        }
        Calendar historyTime= Calendar.getInstance();
        historyTime.setTimeInMillis(lastTime);
        Calendar currentTime= Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());
        if (currentTime.get(Calendar.YEAR)==historyTime.get(Calendar.YEAR)
                &&currentTime.get(Calendar.MONTH)==historyTime.get(Calendar.MONTH)
                &&currentTime.get(Calendar.DAY_OF_MONTH)==historyTime.get(Calendar.DAY_OF_MONTH)){
            return getContext().getResources().getString(R.string.last_refresh_is_today)+historyTime.get(Calendar.HOUR_OF_DAY)+":"+autoAddZero(historyTime.get(Calendar.MINUTE),2);
        }else{
            return getContext().getResources().getString(R.string.last_refresh_time)+historyTime.get(Calendar.YEAR)+getContext().getResources().getString(R.string.year)+(historyTime.get(Calendar.MONTH)+1)+getContext().getResources().getString(R.string.month)
                    +currentTime.get(Calendar.DAY_OF_MONTH)+getContext().getResources().getString(R.string.day)+historyTime.get(Calendar.HOUR_OF_DAY)+":"+autoAddZero(historyTime.get(Calendar.MINUTE),2);
        }
    }

    /**
     * 往下拉一直到刷新
     */
    public void downToRefreshing(){
        onRefreshTag.setVisibility(VISIBLE);
        onRefreshIngPB.setVisibility(GONE);
        onRefreshTag.setText("↓");
        onRefreshHintTV.setText(getContext().getResources().getString(R.string.down_to_refresh));
        onRefreshLastTimeTV.setText(getLastRefreshTime());
    }

    /**
     * 松手刷新
     */
    public void releaseToRefresh(){
        onRefreshTag.setVisibility(VISIBLE);
        onRefreshIngPB.setVisibility(GONE);
        onRefreshTag.setText("↑");
        onRefreshHintTV.setText(getContext().getResources().getString(R.string.up_to_refresh));
        onRefreshLastTimeTV.setText(getLastRefreshTime());
    }

    /**
     * 刷新中
     */
    public void refreshing(){
        onRefreshTag.setVisibility(GONE);
        onRefreshIngPB.setVisibility(VISIBLE);
        onRefreshHintTV.setText(getContext().getResources().getString(R.string.refreshing));
        onRefreshLastTimeTV.setText(getLastRefreshTime());

        Drawable wrapDrawable = DrawableCompat.wrap(onRefreshIngPB.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, progressBarColor);
        onRefreshIngPB.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));

        saveLastRefreshTime(System.currentTimeMillis());
    }
    /**
     * 自动在前方加一个0
     *
     * @param num
     * @param count
     * @return
     */
    public String autoAddZero(Object num, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        if ((num + "").length() < count) {
            for (int i = 0; i < count - (num + "").length(); i++) {
                stringBuilder.append("0");
            }
        }
        stringBuilder.append(num + "");
        return stringBuilder.toString();
    }
}
