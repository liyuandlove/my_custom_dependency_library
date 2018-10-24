package com.yutao.ytutils.ui.topBar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;

/**
 * a：余涛
 * b：1054868047
 * c：2018/9/27 11:05
 * d：
 *         1、可以设置背景颜色；
 *         2、可以设置左边图标；
 *         3、可以设置右边图标；
 *         4、可以设置右边文字；
 *         5、可以设置右边文字颜色，大小；
 *         6、可以设置标题文字颜色，大小；
 *         7、可以设置左边图标、右边图标、右边文字是否显示；
 */
public class TopBar extends RelativeLayout implements View.OnClickListener {
    private LinearLayout topBarContentLL;
    private ImageView topBarLeftIV;
    private TextView topBarTitleTV;
    private TextView topBarRightTV;
    private RelativeLayout topBarRightRL;
    private ImageView topBarRightIV;
    private View topBarDividerView;

    private int topBarBackgroundColor;
    private int topBarLeftIconRId;
    private int topBarRightIconRId;
    private String topBarRightText;
    private String topBarTitleText;
    private int topBarRightTextColor;
    private float topBarRightTextSize;
    private int topBarTitleTextColor;
    private float topBarTitleTextSize;
    private boolean topBarIsShowLeftIcon = true
            ,topBarIsShowRightIcon
            ,topBarIsShowRightText;

    private float topBarLeftIconSize
            ,topBarRightIconSize;

    private OnTopBarClickListener onTopBarClickListener;


    public TopBar(Context context) {
        super(context);
        initViews(null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void findViews(){
        topBarContentLL = findViewById(R.id.topBarContentLL);
        topBarLeftIV = findViewById(R.id.topBarLeftIV);
        topBarTitleTV = findViewById(R.id.topBarTitleTV);
        topBarRightTV = findViewById(R.id.topBarRightTV);
        topBarRightRL = findViewById(R.id.topBarRightRL);
        topBarRightIV = findViewById(R.id.topBarRightIV);

        topBarDividerView = findViewById(R.id.topBarDividerView);

        topBarRightRL.setOnClickListener(this);
        topBarLeftIV.setOnClickListener(this);
    }

    private void initViews(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.view_top_bar,this);
        findViews();

        if (attrs!=null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TopBar);
            topBarBackgroundColor = array.getColor(R.styleable.TopBar_topBarBackgroundColor,Color.WHITE);
            topBarLeftIconRId = array.getResourceId(R.styleable.TopBar_topBarLeftIcon,R.mipmap.icon_left);
            topBarRightIconRId = array.getResourceId(R.styleable.TopBar_topBarRightIcon,R.mipmap.icon_scan_order);
            topBarRightText = array.getString(R.styleable.TopBar_topBarRightText);
            topBarTitleText = array.getString(R.styleable.TopBar_topBarTitleText);
            topBarRightTextColor = array.getColor(R.styleable.TopBar_topBarRightTextColor,ContextCompat.getColor(getContext(),R.color.color_030303));
            topBarRightTextSize = array.getDimensionPixelSize(R.styleable.TopBar_topBarRightTextSize,17);
            topBarTitleTextColor = array.getColor(R.styleable.TopBar_topBarTitleTextColor,ContextCompat.getColor(getContext(),R.color.color_333333));
            topBarTitleTextSize = array.getDimensionPixelSize(R.styleable.TopBar_topBarTitleTextSize,17);

            topBarIsShowLeftIcon = array.getBoolean(R.styleable.TopBar_topBarIsShowLeftIcon,true);
            topBarIsShowRightIcon = array.getBoolean(R.styleable.TopBar_topBarIsShowRightIcon,false);
            topBarIsShowRightText = array.getBoolean(R.styleable.TopBar_topBarIsShowRightText,false);

            topBarLeftIconSize = array.getDimension(R.styleable.TopBar_topBarLeftIconSize,PhoneUtils.dpTopx(getContext(), 25));
            topBarRightIconSize = array.getDimension(R.styleable.TopBar_topBarRightIconSize,PhoneUtils.dpTopx(getContext(), 25));

            array.recycle();
        }

        setTopBarBackgroundColor(topBarBackgroundColor);
        setTopBarLeftIconRId(topBarLeftIconRId);
        setTopBarRightIconRId(topBarRightIconRId);
        setTopBarRightText(topBarRightText);
        setTopBarTitleText(topBarTitleText);
        setTopBarRightTextColor(topBarRightTextColor);
        setTopBarRightTextSize(topBarRightTextSize);
        setTopBarTitleTextColor(topBarTitleTextColor);
        setTopBarTitleTextSize(topBarTitleTextSize);
        setTopBarIsShowLeftIcon(topBarIsShowLeftIcon);
        setTopBarIsShowRightIcon(topBarIsShowRightIcon);
        setTopBarIsShowRightText(topBarIsShowRightText);
        setTopBarLeftIconSize(topBarLeftIconSize);
        setTopBarRightIconSize(topBarRightIconSize);
    }

    public LinearLayout getTopBarContentLL() {
        return topBarContentLL;
    }

    public void setTopBarContentLL(LinearLayout topBarContentLL) {
        this.topBarContentLL = topBarContentLL;
    }

    public ImageView getTopBarLeftIV() {
        return topBarLeftIV;
    }

    public void setTopBarLeftIV(ImageView topBarLeftIV) {
        this.topBarLeftIV = topBarLeftIV;
    }

    public TextView getTopBarTitleTV() {
        return topBarTitleTV;
    }

    public void setTopBarTitleTV(TextView topBarTitleTV) {
        this.topBarTitleTV = topBarTitleTV;
    }

    public TextView getTopBarRightTV() {
        return topBarRightTV;
    }

    public void setTopBarRightTV(TextView topBarRightTV) {
        this.topBarRightTV = topBarRightTV;
    }

    public RelativeLayout getTopBarRightRL() {
        return topBarRightRL;
    }

    public void setTopBarRightRL(RelativeLayout topBarRightRL) {
        this.topBarRightRL = topBarRightRL;
    }

    public ImageView getTopBarRightIV() {
        return topBarRightIV;
    }

    public void setTopBarRightIV(ImageView topBarRightIV) {
        this.topBarRightIV = topBarRightIV;
    }

    public View getTopBarDividerView() {
        return topBarDividerView;
    }

    public void setTopBarDividerView(View topBarDividerView) {
        this.topBarDividerView = topBarDividerView;
    }

    public int getTopBarBackgroundColor() {
        return topBarBackgroundColor;
    }

    public void setTopBarBackgroundColor(int topBarBackgroundColor) {
        this.topBarBackgroundColor = topBarBackgroundColor;
        setBackgroundColor(topBarBackgroundColor);
        if (topBarContentLL == null)
            return;
        topBarContentLL.setBackgroundColor(topBarBackgroundColor);
    }

    public int getTopBarLeftIconRId() {
        return topBarLeftIconRId;
    }

    public void setTopBarLeftIconRId(int topBarLeftIconRId) {
        this.topBarLeftIconRId = topBarLeftIconRId;
        if (topBarLeftIV == null)
            return;
        topBarLeftIV.setImageResource(topBarLeftIconRId);
    }

    public int getTopBarRightIconRId() {
        return topBarRightIconRId;
    }

    public void setTopBarRightIconRId(int topBarRightIconRId) {
        this.topBarRightIconRId = topBarRightIconRId;
        if (topBarRightIV == null)
            return;
        topBarRightIV.setImageResource(topBarRightIconRId);
    }

    public String getTopBarRightText() {
        return topBarRightText;
    }

    public void setTopBarRightText(String topBarRightText) {
        this.topBarRightText = topBarRightText;
        if (topBarRightTV == null)
            return;
        topBarRightTV.setText(topBarRightText);
    }

    public int getTopBarRightTextColor() {
        return topBarRightTextColor;
    }

    public void setTopBarRightTextColor(int topBarRightTextColor) {
        this.topBarRightTextColor = topBarRightTextColor;
        if (topBarRightTV == null)
            return;
        topBarRightTV.setTextColor(topBarRightTextColor);
    }

    public float getTopBarRightTextSize() {
        return topBarRightTextSize;
    }

    public void setTopBarRightTextSize(float topBarRightTextSize) {
        this.topBarRightTextSize = topBarRightTextSize;
        if (topBarRightTV == null)
            return;
        topBarRightTV.setTextSize(topBarRightTextSize);
    }

    public int getTopBarTitleTextColor() {
        return topBarTitleTextColor;
    }

    public void setTopBarTitleTextColor(int topBarTitleTextColor) {
        this.topBarTitleTextColor = topBarTitleTextColor;
        if (topBarTitleTV == null)
            return;
        topBarTitleTV.setTextColor(topBarTitleTextColor);
    }

    public float getTopBarTitleTextSize() {
        return topBarTitleTextSize;
    }

    public void setTopBarTitleTextSize(float topBarTitleTextSize) {
        this.topBarTitleTextSize = topBarTitleTextSize;
        if (topBarTitleTV == null)
            return;
        topBarTitleTV.setTextSize(topBarTitleTextSize);
    }

    public boolean isTopBarIsShowLeftIcon() {
        return topBarIsShowLeftIcon;
    }

    public void setTopBarIsShowLeftIcon(boolean topBarIsShowLeftIcon) {
        this.topBarIsShowLeftIcon = topBarIsShowLeftIcon;
        if (topBarLeftIV == null)
            return;
        if (topBarIsShowLeftIcon){
            topBarLeftIV.setVisibility(VISIBLE);
        }else{
            topBarLeftIV.setVisibility(GONE);
        }
    }

    public boolean isTopBarIsShowRightIcon() {
        return topBarIsShowRightIcon;
    }

    public void setTopBarIsShowRightIcon(boolean topBarIsShowRightIcon) {
        this.topBarIsShowRightIcon = topBarIsShowRightIcon;
        if (topBarRightRL == null
                ||topBarRightIV == null)
            return;
        if (topBarIsShowRightIcon){
            topBarRightIV.setVisibility(VISIBLE);
            topBarRightRL.setVisibility(VISIBLE);
        }else{
            topBarRightIV.setVisibility(GONE);
            if (!topBarIsShowRightText)
                topBarRightRL.setVisibility(GONE);
        }
    }

    public boolean isTopBarIsShowRightText() {
        return topBarIsShowRightText;
    }

    public void setTopBarIsShowRightText(boolean topBarIsShowRightText) {
        this.topBarIsShowRightText = topBarIsShowRightText;
        if (topBarRightRL == null
                ||topBarRightTV == null)
            return;
        if (topBarIsShowRightText){
            topBarRightTV.setVisibility(VISIBLE);
            topBarRightRL.setVisibility(VISIBLE);
        }else{
            topBarRightTV.setVisibility(GONE);
            if (!topBarIsShowRightIcon)
                topBarRightRL.setVisibility(GONE);
        }
    }

    public String getTopBarTitleText() {
        return topBarTitleText;
    }

    public void setTopBarTitleText(String topBarTitleText) {
        this.topBarTitleText = topBarTitleText;
        if (topBarTitleTV == null)
            return;
        topBarTitleTV.setText(topBarTitleText);
    }

    public float getTopBarLeftIconSize() {

        return topBarLeftIconSize;
    }
    @Deprecated
    public void setTopBarLeftIconSize(float topBarLeftIconSize) {
        this.topBarLeftIconSize = topBarLeftIconSize;
        if (topBarLeftIV == null){
            return;
        }
    }

    public float getTopBarRightIconSize() {
        return topBarRightIconSize;
    }

    public void setTopBarRightIconSize(float topBarRightIconSize) {
        this.topBarRightIconSize = topBarRightIconSize;
        if (topBarRightIV == null){
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) topBarRightIV.getLayoutParams();
        if (layoutParams == null)
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.height = (int) topBarRightIconSize;
        layoutParams.width = (int) topBarRightIconSize;

        topBarRightIV.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        if (onTopBarClickListener==null)
            return;
        int i = v.getId();
        if (i == R.id.topBarRightRL) {
            if (!onTopBarClickListener.onTopBarClick(false,v)){

            }
        }else if (i == R.id.topBarLeftIV){
            if (!onTopBarClickListener.onTopBarClick(true,v)){
                if (getContext() instanceof Activity){
                    ((Activity) getContext()).finish();
                }
            }
        }
    }

    public OnTopBarClickListener getOnTopBarClickListener() {
        return onTopBarClickListener;
    }

    public void setOnTopBarClickListener(OnTopBarClickListener onTopBarClickListener) {
        this.onTopBarClickListener = onTopBarClickListener;
    }

    public interface OnTopBarClickListener{
        /**
         * 返回 false 表示不拦截，返回 ture 表示拦截
         * @param isLeft
         * @param view
         * @return
         */
        boolean onTopBarClick(boolean isLeft,View view);
    }
}
