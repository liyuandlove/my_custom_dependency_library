package com.yutao.ytutils.ui.customswiperefresh;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.yutao.ytutils.R;


/**
 * 上拉加载以及下拉刷新自定义控件
 */
public class CustomSwipeRefreshLayout extends LinearLayout {
    private String TAG=CustomSwipeRefreshLayout.class.getSimpleName();

    private ViewGroup refreshView//刷新视图
            ,onloadView//加载视图
            ;
    private View contentView;//内容视图
    private float downY//按下的Y轴坐标
            ;
    private float currentY//当前的Y轴坐标
            ;
    private float interceptDownY//拦截时使用的按下Y轴坐标
            ;
    private float interceptcurrentY//拦截时使用的当前Y轴坐标
            ;
    private int touchSlop;//认定为滑动的最小距离

    private boolean isCanRefresh = true;
    private boolean isCanLoadMore = true;
    private boolean isOnRefreshing = false;
    private boolean isOnloading = false;

    private boolean isCanGoRefresh = false;
    private boolean isCanGoLoad = false;

    private boolean isCanDownScroll=false;
    private boolean isCanUpScroll =false;

    private int screenWidth
            ,screenHeight
            ;

    private int contentHeight;

    private OnRefreshOnLoadListener onRefreshOnLoadListener;
    private OnScrollEventListener onScrollEventListener;
    //    private int refreshHeight;//刷新视图的高度
    private int topBackgroundColor = Color.WHITE
            ,topTextColor = Color.BLACK
            ,bottomBackgroundColor = Color.WHITE
            ,bottomTextColor = Color.BLACK
            ,progressBarColorTop = Color.parseColor("#1E90FA")
            ,progressBarColorBottom  = Color.parseColor("#1E90FA")
            ;

    private boolean isOnTouching = false;//是否正在触摸中


    public CustomSwipeRefreshLayout(Context context) {
        super(context);
        init(null);
    }

    public CustomSwipeRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomSwipeRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        isNeedInteceptTouch(event);

        boolean isOver = super.onTouchEvent(event);

        isOver = true;

        return isOver;
    }

    public boolean isOnTouching() {
        return isOnTouching;
    }

    public void setOnTouching(boolean onTouching) {
        isOnTouching = onTouching;
    }

    @Override

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isNeedInteceptTouch(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isNeedInteceptTouch(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs){
        screenWidth=getContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight=getContext().getResources().getDisplayMetrics().heightPixels;

        refreshViewEndHeight = dp2px(50);

        setOrientation(VERTICAL);

        refreshView = new OnRefreshView(getContext());
        onloadView = new OnLoadView(getContext());

        if (attrs!=null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomSwipeRefreshLayout);
            topBackgroundColor = array.getColor(R.styleable.CustomSwipeRefreshLayout_topBackgroundColor, Color.WHITE);
            topTextColor = array.getColor(R.styleable.CustomSwipeRefreshLayout_topTextColor, Color.BLACK);
            bottomBackgroundColor = array.getColor(R.styleable.CustomSwipeRefreshLayout_bottomBackgroundColor, Color.WHITE);
            bottomTextColor = array.getColor(R.styleable.CustomSwipeRefreshLayout_bottomTextColor, Color.BLACK);
            progressBarColorTop = array.getColor(R.styleable.CustomSwipeRefreshLayout_topProgressColor,progressBarColorTop);
            progressBarColorBottom = array.getColor(R.styleable.CustomSwipeRefreshLayout_bottomProgressColor,progressBarColorBottom);

            isCanRefresh = array.getBoolean(R.styleable.CustomSwipeRefreshLayout_canRefresh,true);
            isCanLoadMore = array.getBoolean(R.styleable.CustomSwipeRefreshLayout_canOnload,true);

            setTopBackgroundColor(topBackgroundColor);
            setTopTextColor(topTextColor);
            setBottomBackgroundColor(bottomBackgroundColor);
            setBottomTextColor(bottomTextColor);
            setProgressBarColorTop(progressBarColorTop);
            setProgressBarColorBottom(progressBarColorBottom);

            array.recycle();
        }

        touchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();

        this.addView(refreshView);
//        this.addView(onloadView);
        currentView = refreshView;

        setChidenHeightAnim(refreshView,0);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDetachedFromWindow() {//当view回收时将字体还原
        setBottomTextColor(Color.BLACK);
        setTopTextColor(Color.BLACK);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initContentView();
    }

    private void initContentView(){
        LayoutParams layoutParams = (LayoutParams) getContentView().getLayoutParams();
        if (layoutParams==null)
            layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        else{
            if (layoutParams.weight==1&&layoutParams.height== LayoutParams.WRAP_CONTENT)
                return;
        }
        layoutParams.weight=1;
        getContentView().setLayoutParams(layoutParams);
    }

    /**
     * 得到子布局的视图配置
     * @return
     */
    public LayoutParams getChildLayoutParams(){
        LayoutParams layoutParams = (LayoutParams) getContentView().getLayoutParams();
        if (layoutParams==null)
            layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        else{
            if (layoutParams.weight==1&&layoutParams.height== LayoutParams.WRAP_CONTENT)
                return layoutParams;
        }
        layoutParams.weight=1;
        return layoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            super.onLayout(changed, l, t, r, b);
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "onLayout: ", e);
        }
    }

    /**
     * 获得内容视图
     * @return
     */
    private View getContentView(){
        if (getChildCount()>=2
                &&getChildAt(1)!=null
                &&!(getChildAt(1) instanceof OnRefreshView)
                ){
            if (contentView == null
                    ||contentView != getChildAt(1)){
                contentView = getChildAt(1);
                initContentView();
            }
            return contentView;
        }else{
            return null;
        }
    }

    /**
     * 判断是否内容视图是否可以继续往下滚动
     *
     * contentView.canScrollVertically(1)==false 时 无法继续向上滚动，此时可以加载更多
     * contentView.canScrollVertically(-1)==false 时 无法继续向下滚动，此时可以刷新数据
     */
    public boolean isCanDownScroll(){
        if (getContentView()==null)
            return false;
        View contentView=getContentView();
//        Log.d(TAG, "isCanDownScroll down: "+contentView.canScrollVertically(-1)+" "+contentView.canScrollVertically(1));
        if (!contentView.canScrollVertically(-1)){//刷新数据
            return false;
        }
        return true;
    }

    /**
     * 判断是否可以继续向下滚动
     * @return
     */
    public boolean isCanUpScroll(){
        if (getContentView()==null)
            return false;
        View contentView=getContentView();
//        Log.d(TAG, "isCanDownScroll up: "+contentView.canScrollVertically(-1)+" "+contentView.canScrollVertically(1));
        if (!contentView.canScrollVertically(1)){//刷新数据
            return false;
        }
        return true;
    }

    public void setOnScrollEventListener(OnScrollEventListener onScrollEventListener) {
        this.onScrollEventListener = onScrollEventListener;
    }

    /**
     * 判断是否需要拦截
     * @return
     */
    private boolean isScrolled = false;
    private boolean isNeedInteceptTouch(MotionEvent event){
        if (event.getPointerCount()!=1)
            return false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isOnTouching = true;
                isScrolled = false;
                downY = event.getRawY();
                isCanDownScroll=isCanDownScroll();
                isCanUpScroll=isCanUpScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                isOnTouching = true;
                currentY = event.getRawY();

                if (Math.abs(currentY-downY)>touchSlop)
                    isScrolled = true;

                if (isScrolled) {
                    if (onScrollEventListener != null) {
                        onScrollEventListener.onScroll(currentY - downY);
//                    onScrollEventListener.onViewSizeChange(currentView,(int) (Math.abs(currentY - downY) / 3));
                    }

                    if (!isOnRefreshing()
                            && !isOnloading()) {
//                        if (isScrolled) {//此时判定为滑动
                        if (currentY > downY && !isOnloading) {
                            if (!isCanDownScroll) {//无法继续往下滚动了
                                if (isCanRefresh()) {
                                    if (!showRefreshView()) {
                                        currentView = refreshView;
                                        setChidenHeightNoAnim(refreshView, (int) (Math.abs(currentY - downY) / 3));
                                    }
                                }
                                return true;
                            }
                        }
                        if (currentY < downY && !isOnRefreshing) {
                            if (!isCanUpScroll) {//无法继续往上滚动了
                                if (isCanLoadMore()) {
                                    if (!showOnloadView()) {
                                        currentView = onloadView;
                                        setChidenHeightNoAnim(onloadView, (int) (Math.abs(downY - currentY) / 3));
                                    }
                                }
                                return true;
                            }
                        }
//                        }
                    }
                }
                break;
            default:
                downY = 0;
                currentY = 0;
                contentHeight = 0;
                isOnTouching = false;
                if (isScrolled)
                    dealTouchUp();
                break;
        }

        return false;
    }

    private int chidenHeight;
    private View currentView;
    private ObjectAnimator objectAnimator;
    private float refreshViewEndHeight = 0;

    public void setChidenHeight(int chidenHeight) {
        this.chidenHeight = chidenHeight;

        setChidenHeightNoAnim(currentView, chidenHeight);
        if (onScrollEventListener!=null){
            onScrollEventListener.onViewSizeChange(contentView,chidenHeight);
        }
    }

    public int getChidenHeight() {
        return chidenHeight;
    }

    public void setChidenHeightAnim(View view, int height){
        int startFloat = 0;
        if (view.getLayoutParams()!=null)
            startFloat = view.getHeight();

        if (objectAnimator!=null&&objectAnimator.isRunning()){
            objectAnimator.end();
            objectAnimator = null;
        }

//        if (startFloat == height)
//            return;

        if (currentView!=null)
            Log.d(TAG, "setChidenHeightAnim: "+view.getClass().toString()+" \n"+currentView.getHeight()+" "+currentView.getMeasuredHeight()+" "+startFloat+" "+height+" "+refreshViewEndHeight);

//        objectAnimator = ObjectAnimator.ofInt(this,"chidenHeight",startFloat,0);
        objectAnimator = ObjectAnimator.ofInt(this,"chidenHeight",new int[]{startFloat,height});
//        objectAnimator.setupEndValues();
        objectAnimator.setDuration(250);
        objectAnimator.start();
    }

    /**
     * 设置高度
     * @param height
     */
    public void setChidenHeightNoAnim(View view, int height){
        if (view == null)
            return;

        if (view.getLayoutParams()==null)
            view.setLayoutParams(new MarginLayoutParams(screenWidth,0));

        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();

        if(layoutParams.height == height)
            return;

        layoutParams.height=height;
        view.setLayoutParams(layoutParams);

        Log.d(TAG, "setChidenHeightNoAnim: "+view.getClass().toString()+" "+height+" "+(getContentView()==null?0:getContentView().getTop()));
    }

    public boolean isOnRefreshing() {
        return isOnRefreshing;
    }

    public void setOnRefreshing(boolean onRefreshing) {
        isOnRefreshing = onRefreshing;

        if (isOnTouching){
            Log.d(TAG, "setChidenHeightAnim: 正在触摸中，取消操作");
            return;
        }

        if (!isOnRefreshing
                &&refreshView!=null){
            setChidenHeightAnim(refreshView,0);
        }else if (isOnRefreshing&&refreshView!=null){
            setChidenHeightAnim(refreshView, (int) refreshViewEndHeight);
            if (refreshView instanceof OnRefreshView)
                ((OnRefreshView) refreshView).refreshing();
//            if (onRefreshOnLoadListener!=null)
//                onRefreshOnLoadListener.onRefresh(0);
        }
    }

    public boolean isOnloading() {
        return isOnloading;
    }

    public void setOnloading(boolean onloading) {
        isOnloading = onloading;
        if (!isOnloading
                &&onloadView!=null){
            if (onloadView instanceof OnLoadView){
                ((OnLoadView) onloadView).endOnload();
            }
            setChidenHeightAnim(onloadView,0);
        }
    }

    public boolean isCanRefresh() {
        if (isOnloading())
            return false;
        return isCanRefresh;
    }

    public void setCanRefresh(boolean canRefresh) {
        isCanRefresh = canRefresh;
    }

    public boolean isCanLoadMore() {
        if (isOnRefreshing())
            return false;
        return isCanLoadMore;
    }

    public int getProgressBarColorTop() {
        return progressBarColorTop;
    }

    public void setProgressBarColorTop(int progressBarColorTop) {
        this.progressBarColorTop = progressBarColorTop;
        if (refreshView instanceof OnRefreshView){
            ((OnRefreshView) refreshView).setProgressBarColor(progressBarColorTop);
        }

    }

    public int getProgressBarColorBottom() {
        return progressBarColorBottom;
    }

    public void setProgressBarColorBottom(int progressBarColorBottom) {
        this.progressBarColorBottom = progressBarColorBottom;
        if (onloadView instanceof OnLoadView){
            ((OnLoadView) onloadView).setProgressBarColor(progressBarColorBottom);
        }
    }

    public int getTopBackgroundColor() {
        if (refreshView instanceof OnRefreshView){
            topBackgroundColor = ((OnRefreshView) refreshView).getBackgroundColor();
        }
        return topBackgroundColor;
    }

    public void setTopBackgroundColor(int topBackgroundColor) {
        this.topBackgroundColor = topBackgroundColor;
        if (refreshView instanceof OnRefreshView){
            refreshView.setBackgroundColor(topBackgroundColor);
        }
    }

    public int getTopTextColor() {
        if (refreshView instanceof OnRefreshView){
            topTextColor = ((OnRefreshView) refreshView).getTextColor();
        }
        return topTextColor;
    }

    public void setTopTextColor(int topTextColor) {
        this.topTextColor = topTextColor;
        if (refreshView instanceof OnRefreshView){
            ((OnRefreshView) refreshView).setTextColor(topTextColor);
        }
    }

    public int getBottomBackgroundColor() {
        if (onloadView instanceof OnLoadView){
            bottomBackgroundColor = ((OnLoadView) onloadView).getBackgroundColor();
        }
        return bottomBackgroundColor;
    }

    public void setBottomBackgroundColor(int bottomBackgroundColor) {
        this.bottomBackgroundColor = bottomBackgroundColor;
        if (onloadView instanceof OnLoadView){
            onloadView.setBackgroundColor(bottomBackgroundColor);
        }
    }

    public int getBottomTextColor() {
        if (onloadView instanceof OnLoadView) {
            bottomTextColor = ((OnLoadView) onloadView).getTextColor();
        }
        return bottomTextColor;
    }

    public void setBottomTextColor(int bottomTextColor) {
        this.bottomTextColor = bottomTextColor;
        if (onloadView instanceof OnLoadView) {
            ((OnLoadView) onloadView).setTextColor(bottomTextColor);
        }
    }

    public void setCanLoadMore(boolean canLoadMore) {
        isCanLoadMore = canLoadMore;
    }

    /**
     * 展现刷新视图,如果视图的高度已经和子视图的高度一样时(表示展现完成)，则不需要再往下展现了
     */
    public boolean showRefreshView(){
        if (refreshView==null)
            return false;
        if (refreshView instanceof OnRefreshView) {
            if (refreshView.getHeight() > refreshViewEndHeight) {
                isCanGoRefresh=true;
                ((OnRefreshView) refreshView).releaseToRefresh();//显示释放去刷新
            }else{
                isCanGoRefresh=false;
                ((OnRefreshView) refreshView).downToRefreshing();//显示继续下拉刷新
            }
        }
        return false;
    }

    public boolean showOnloadView(){
        if (onloadView==null)
            return false;
        int index=indexOfChild(onloadView);
        if (index==-1) {
//            Log.d(TAG, "showOnloadView: ");
            this.removeView(onloadView);
            this.addView(onloadView, getChildCount());
        }
        if (getContentView() instanceof RecyclerView){
            if (((RecyclerView) getContentView()).getAdapter()!=null)
                ((RecyclerView) getContentView()).smoothScrollToPosition(((RecyclerView) getContentView()).getAdapter().getItemCount());
        }
        if (getContentView() instanceof ScrollView){
            ScrollView scrollView= (ScrollView) getContentView();
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滑动到底部
        }

        if (onloadView instanceof OnLoadView){
            if (onloadView.getHeight() > refreshViewEndHeight) {
                isCanGoLoad=true;
                ((OnLoadView) onloadView).releaseToOnload();//显示释放去加载更多
            }else{
                isCanGoLoad = false;
                ((OnLoadView) onloadView).showUpToOnload();//显示向上拉加载更多
            }
        }

        return false;
    }

    public void dealTouchUp(){
        if (isOnRefreshing
                ||isOnloading)
            return;
//        isCanDownScroll=isCanDownScroll();
//        isCanUpScroll=isCanUpScroll();

        setOnRefreshing(isOnRefreshing());

        if (!isCanDownScroll) {
            if (!isCanGoRefresh && currentView == refreshView) {
                if (refreshView != null) {
                    setChidenHeightAnim(refreshView, 0);
                }
            } else {
                if (refreshView != null && isCanGoRefresh) {
                    if (refreshView instanceof OnRefreshView) {
                        ((OnRefreshView) refreshView).refreshing();
                    }
                    setChidenHeightAnim(refreshView, (int) refreshViewEndHeight);

                    setOnRefreshing(true);//设置正在刷新

                    if (onRefreshOnLoadListener != null)
                        onRefreshOnLoadListener.onRefresh();

//                Log.d(TAG, "dealTouchUp: 刷新");

                    isCanGoRefresh = false;
                }
            }
        }
        if (!isCanUpScroll) {
            if (!isCanGoLoad && currentView == onloadView) {
                if (onloadView != null) {
                    setChidenHeightAnim(onloadView, 0);
                }
            } else {
                if (onloadView != null && isCanGoLoad) {
                    if (onloadView instanceof OnLoadView) {
                        ((OnLoadView) onloadView).loading();
                    }
                    setChidenHeightAnim(onloadView, (int) refreshViewEndHeight);

                    setOnloading(true);//设置正在刷新

                    if (onRefreshOnLoadListener != null)
                        onRefreshOnLoadListener.onLoad();

//                Log.d(TAG, "dealTouchUp: 加载");

                    isCanGoLoad = false;
                }
            }
        }
    }

    public float getRefreshViewEndHeight() {
        return refreshViewEndHeight;
    }

    public ViewGroup getRefreshView() {
        return refreshView;
    }

    public void setRefreshView(ViewGroup refreshView) {
        this.refreshView = refreshView;
    }

    public ViewGroup getOnloadView() {
        return onloadView;
    }

    public void setOnloadView(ViewGroup onloadView) {
        this.onloadView = onloadView;
    }

    public void setOnRefreshOnLoadListener(OnRefreshOnLoadListener onRefreshOnLoadListener) {
        this.onRefreshOnLoadListener = onRefreshOnLoadListener;
    }

    public interface OnRefreshOnLoadListener{
        void onLoad();
        void onRefresh();
    }

    public interface OnScrollEventListener{
        void onScroll(float offset);
        void onViewSizeChange(View view,float changeSize);
    }

    protected int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
