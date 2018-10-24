package com.yutao.ytutils.ui.morefunlist;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;
import com.yutao.ytutils.ui.customswiperefresh.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/15 11:47
 * d：更多功能的list
 *
 * 自定义属性:TopIndex : 顶部视图的编号
 *           Divider : 之间的间隔视图
 *           TitleView : 标题视图，标题视图可以选择是否悬停
 *           TopBackground : 头部背景视图，只放在头部的视图，可以设置是否背景暗色
 */
public class MoreFunListView extends FrameLayout implements CustomSwipeRefreshLayout.OnScrollEventListener, MoreFunListAdapter.OnViewHolderVisableChangeListener, View.OnLayoutChangeListener, MoreFunTitleViewGrop.OnViewVisbilityChangeListener {
    private String TAG = "MoreFunListView";

    private boolean topBackgroundDark = true;//顶部背景是否暗下来，默认暗下来
    private boolean titleViewIsHover = true;//标题视图是否悬停，默认悬停

    private View dividerView;//视图之间的间隔视图
    private int dividerId;
    private List<View> titleView;//视图的标题视图
    private List<View> topBackgroundView;//顶部背景视图
    private List<View> contentView;//内容视图
    private List<View> views;//除了背景视图其他的视图列表的集合

    private MoreFunListAdapter moreFunListAdapter;
    private MoreFunRecycleView moreFunRecycleView;
    private MoreFunSizeChangeViewGroup topBackgroundContentRL;
    private LinearLayout moreFunTitleViewContentLL;
    private LinearLayout topBackgroundContentLL;
    private View topBackgroundDarkView;
    private CustomSwipeRefreshLayout moreFunRefreshLayout;
    private CustomSwipeRefreshLayout.OnRefreshOnLoadListener onRefreshOnLoadListener;

    private int slopTouch;
    private float currentX, downY;
    private float offsetY;
    private float endOffsetY = 0;//结束时的偏移量
    private float fullOffsetY = 300;//偏移量的最大偏移量，在初始化的时候重新赋值了
    private int animatorD = 500;
    private float backgroundMaxScale = 0.5f;//背景的最大放大比例
    private float currentBackgroundScale = 1f;//当前的背景放大比例

    private float topPaddingNoBG;//顶部的间距背景不参与

    private ObjectAnimator offsetYAnim;

    private View locationView;
    private float topBackgroundViewByLocationY = 0;
    private boolean isByLoactionViewScale = true;

    private CustomStaggeredGridLayoutManager customStaggeredGridLayoutManager;

    public MoreFunListView(@NonNull Context context) {
        super(context);
    }

    public MoreFunListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public MoreFunListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new MoreFunLayoutParams(getContext(),attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isByLoactionViewScale)
            changeTopBackgroundViewByLocation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        initViewsType();
        initViewsList();
        reLayoutViews();
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        dealTouch(ev);
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        dealTouch(event);
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount()==1) {
            dealTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 处理点击事件
     * @param ev
     */
    private void dealTouch(MotionEvent ev){
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                currentX = ev.getX();
                downY = ev.getY();
                //实现加载视图的平滑处理
                if (topBackgroundContentRL!=null)
                    currentBackgroundScale = topBackgroundContentRL.getScaleX();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((ev.getY() - downY) > slopTouch) {//表示为滑动
                    if (moreFunRecycleView != null) {
                        if (!moreFunRecycleView.canScrollVertically(-1)) {//不能下拉了
                            canNotDownAndOffset(
                                    endOffsetY + (ev.getY() - downY) / 6
                            );
                        }
                    }
                }
                break;
            default:
                currentX = 0;
                downY = 0;
                moreFunRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        canNotDownAndOffsetAnimator(endOffsetY);
                    }
                });
                break;
        }
    }

    /**
     * 不能下来的时候设置偏移量的
     * @param offsetY
     */
    private void canNotDownAndOffset(float offsetY){
        if (moreFunRecycleView == null)
            return;
        if (offsetY < 0)
            return;
        if (offsetY>fullOffsetY)
            offsetY = fullOffsetY;

        //当正在刷新或者不可以刷新的时候才处理这个
//        if (
//                moreFunRefreshLayout.isOnRefreshing()
//                ||!moreFunRefreshLayout.isCanRefresh()
//                !moreFunRefreshLayout.isOnTouching()
//                ||moreFunRefreshLayout.isOnRefreshing()
//                !(
//                        moreFunRefreshLayout.isCanUpScroll()
//                        &&
//                        moreFunRefreshLayout.isCanDownScroll()
//                )
//                ) {
        Log.d(TAG, "canNotDownAndOffset: "+moreFunRecycleView.canScrollVertically(-1));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) moreFunRecycleView.getLayoutParams();
        layoutParams.topMargin = (int) offsetY;
        moreFunRecycleView.setLayoutParams(layoutParams);

        if (!isByLoactionViewScale) {
            changeTopBackgroundView(offsetY);
        }
//        }
    }

    /**
     * 带动画的
     * @param offsetY
     */
    private void canNotDownAndOffsetAnimator(float offsetY){
        if (moreFunRecycleView == null)
            return;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) moreFunRecycleView.getLayoutParams();

        if (layoutParams.topMargin==(int)offsetY)
            return;

        Log.d(TAG, "canNotDownAndOffsetAnimator: "+layoutParams.topMargin+" "+offsetY);

        offsetYAnim = ObjectAnimator.ofFloat(MoreFunListView.this,"offsetY",new float[]{layoutParams.topMargin,offsetY});
        offsetYAnim.setDuration(animatorD);
        offsetYAnim.start();
    }

    /**
     * 改变顶部背景
     */
    private void changeTopBackgroundView(float offsetY){
        if (topBackgroundContentRL==null)
            return;

        float scale = (offsetY)/fullOffsetY*backgroundMaxScale + currentBackgroundScale;

        if (scale>(backgroundMaxScale+1))
            scale = backgroundMaxScale+1;

        topBackgroundContentRL.setScaleX(scale);
        topBackgroundContentRL.setScaleY(scale);
    }


    /**
     * 设置位置视图，用来计算顶部背景的放大比例的
     * @param locationView
     */
    public void setLocationView(View locationView){
        this.locationView = locationView;
        if(locationView!=null) {
            topBackgroundViewByLocationY = locationView.getTop();
        }
    }

    private void changeTopBackgroundViewByLocation(){
        if (topBackgroundViewByLocationY == 0)//赋初始值
            topBackgroundViewByLocationY = locationView.getTop();

        if (locationView == null)
            return;

        float offset = locationView.getTop() - topBackgroundViewByLocationY;
        if (offset<0)
            return;

        float scale =(topBackgroundContentRL.getHeight()+offset*2)
                /topBackgroundContentRL.getHeight();

        if (topBackgroundContentRL.getScaleX() == scale)
            return;

        Log.d(TAG, "changeTopBackgroundViewByLocation: "+locationView.getTop()+" "+topBackgroundViewByLocationY+" "+offset);

        topBackgroundContentRL.setScaleX(scale);
        topBackgroundContentRL.setScaleY(scale);
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        canNotDownAndOffset(offsetY);
    }

    private void initViews(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.MoreFunListView);
        topBackgroundDark = typedArray.getBoolean(R.styleable.MoreFunListView_topBackgroundDark,true);
        titleViewIsHover = typedArray.getBoolean(R.styleable.MoreFunListView_titleViewIsHover,true);
        dividerId = typedArray.getResourceId(R.styleable.MoreFunListView_dividerId,0);
        topPaddingNoBG = typedArray.getDimension(R.styleable.MoreFunListView_topPaddingNoBG,0);

        typedArray.recycle();

        slopTouch = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        fullOffsetY = PhoneUtils.dpTopx(getContext(),200);
    }

    /**
     * 初始化视图的类型
     *   <enum name="contentView" value="-1"/>
     *             <enum name="titleView" value="1"/>
     *             <enum name="topBackground" value="2"/>
     */
    private void initViewsType(){
        int childCount = getChildCount();
        if (childCount != 0){
            for (int i = 0;i<childCount;i++){
                if (getChildAt(i)==null)
                    continue;
                MoreFunLayoutParams moreFunLayoutParams = (MoreFunLayoutParams) getChildAt(i).getLayoutParams();
                switch (moreFunLayoutParams.getViewType()){
                    case -1:
                        addContentView(getChildAt(i));
                        break;
                    case 0:
                        break;
                    case 1:
                        addTitleView(getChildAt(i));
                        break;
                    case 2:
                        addTopBackgroundView(getChildAt(i));
                        break;
                }
            }
        }
    }

    /**
     * 初始化视图的列表，用来在列表上显示的
     */
    private void initViewsList(){
        views = new ArrayList<>();

        if (getContentView()!=null){
            for(int contentIndex = 0;contentIndex<getContentView().size();contentIndex++){
                views.add(getContentView().get(contentIndex));

                addDividerView();
            }
        }

        if (getTitleView()!=null){
            for (int titleIndex = 0;titleIndex<getTitleView().size();titleIndex++){
                MoreFunLayoutParams moreFunLayoutParams = (MoreFunLayoutParams) getTitleView().get(titleIndex).getLayoutParams();

                views.add(moreFunLayoutParams.getTitleIndex()<views.size()?moreFunLayoutParams.getTitleIndex():(views.size())
                        ,getTitleView().get(titleIndex));

                addDividerView();
            }
        }
    }

    private void addDividerView() {
        if (views!=null&&dividerId!=0){
            View dividerView = LayoutInflater.from(getContext()).inflate(dividerId,null);
            this.dividerView = dividerView;
            views.add(dividerView);
        }
    }

    private void reLayoutViews(){
        Log.d(TAG, "reLayoutViews: ");

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_more_fun_list_layout,null);

        moreFunRecycleView = contentView.findViewById(R.id.moreFunRecycleView);
        topBackgroundContentRL = contentView.findViewById(R.id.topBackgroundContentRL);
        topBackgroundDarkView = contentView.findViewById(R.id.topBackgroundDarkView);
        topBackgroundContentLL = contentView.findViewById(R.id.topBackgroundContentLL);
        moreFunRefreshLayout = contentView.findViewById(R.id.moreFunRefreshLayout);
        moreFunTitleViewContentLL = contentView.findViewById(R.id.moreFunTitleViewContentLL);

        setLocationView(moreFunRecycleView);

        customStaggeredGridLayoutManager = new CustomStaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        moreFunRecycleView.setLayoutManager(customStaggeredGridLayoutManager);

        moreFunRefreshLayout.setPadding(moreFunRefreshLayout.getPaddingLeft()
                , (int) topPaddingNoBG
                ,moreFunRefreshLayout.getPaddingRight(),moreFunRefreshLayout.getPaddingBottom());

        moreFunRefreshLayout.setOnScrollEventListener(this);
        moreFunRefreshLayout.setOnRefreshOnLoadListener(onRefreshOnLoadListener);
        moreFunRecycleView.addOnLayoutChangeListener(this);

        topBackgroundContentRL.setOnSizeChangeListener(new MoreFunSizeChangeViewGroup.OnSizeChangeListener() {
            @Override
            public void onSizeChange(MoreFunSizeChangeViewGroup viewGroup, int w, final int h, int oldw, int oldh) {
                if (topBackgroundDarkView == null)
                    return;
                Log.d(TAG, "onSizeChange: "+h);
                topBackgroundDarkView.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = topBackgroundDarkView.getLayoutParams();
                        layoutParams.height = h;
                        topBackgroundDarkView.setLayoutParams(layoutParams);
                    }
                });
            }
        });

        moreFunRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (tempView!=null
                        &&dy<0){//向下滑动
                    int[] location = new int[2];
                    int[] locationParent = new int[2];

                    tempView.getLocationOnScreen(location);
                    if(tempView.getParent()!=null
                            &&tempView.getParent() instanceof ViewGroup){
                        ((ViewGroup) tempView.getParent()).getLocationOnScreen(locationParent);
                    }

                    if (!(tempView.getTag() instanceof MoreFunListAdapter.MoreFunViewHolder))
                        return;

                    MoreFunListAdapter.MoreFunViewHolder viewHolder = (MoreFunListAdapter.MoreFunViewHolder) tempView.getTag();
                    if (moreFunTitleViewContentLL.indexOfChild(views.get(viewHolder.getAdapterPosition()))!=-1) {
                        if (location[1] == locationParent[1] && locationParent[1] == 0) {
                            return;
                        }
                        if (location[1] >= (locationParent[1])) {
                            if (viewHolder instanceof MoreFunListAdapter.MoreFunViewHolder
                                    && tempView instanceof MoreFunTitleViewGrop) {
                                if (((ViewGroup) viewHolder.itemView).indexOfChild(views.get(viewHolder.getAdapterPosition())) == -1) {
                                    //表示该视图不存在在viewholder中了
                                    Log.d(TAG, "onScrolled: " + location[1]
                                            + " " + views.get(viewHolder.getAdapterPosition()).getMeasuredHeight()
                                            + " " + locationParent[1]
                                            +" "+dx
                                            +" "+dy
                                    );

                                    (viewHolder).addView(views.get(viewHolder.getAdapterPosition()), MoreFunListView.this);
                                }
                            }

                        }
                    }
                }

            }
        });

        this.addView(contentView);

        int topBackgroundHeight = 0;

        if (getTopBackgroundView()!=null){
            for (View topBackView:getTopBackgroundView()){
                if (topBackView.getParent() instanceof ViewGroup)
                    ((ViewGroup) topBackView.getParent()).removeView(topBackView);

                topBackgroundContentLL.addView(topBackView);
                topBackgroundHeight = topBackgroundHeight + topBackView.getMeasuredHeight()==0
                        ?topBackView.getLayoutParams().height:topBackView.getMeasuredHeight();
            }
        }

        if (isTopBackgroundDark()){
            topBackgroundDarkView.setVisibility(VISIBLE);
            topBackgroundDarkView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    ,topBackgroundHeight
            ));
        }else{
            topBackgroundDarkView.setVisibility(GONE);
        }

        initMoreFunAdapter();
    }

    /**
     * 初始化更多功能视图的adapter
     */
    private void initMoreFunAdapter(){
        if (moreFunListAdapter == null) {
            moreFunListAdapter = new MoreFunListAdapter(getContext(), views,getTitleView());
            moreFunListAdapter.setOnViewHolderVisableChangeListener(this);
            moreFunListAdapter.setOnViewVisbilityChangeListener(this);
        }
        if (moreFunRecycleView!=null){
            moreFunRecycleView.setAdapter(moreFunListAdapter);
        }
        moreFunListAdapter.notifyDataSetChanged();
    }


    /**
     * 是否顶部背景暗色
     * @return
     */
    public boolean isTopBackgroundDark() {
        return topBackgroundDark;
    }

    /**
     * 添加内容视图
     * @param view
     */
    public void addContentView(int index,View view){
        if (getContentView() == null){
            setContentView(new ArrayList<View>());
        }
        getContentView().add(index,view);
    }


    /**
     * 添加标题视图
     * @param index
     * @param view
     */
    public void addTitleView(int index,View view){
        if (getTitleView() == null){
            setTitleView(new ArrayList<View>());
        }
        getTitleView().add(index,view);
    }

    /**
     * 添加顶部背景视图
     * @param index
     * @param view
     */
    public void addTopBackgroundView(int index,View view){
        if (getTopBackgroundView() == null){
            setTopBackgroundView(new ArrayList<View>());
        }
        getTopBackgroundView().add(index,view);
    }

    public void addContentView(View view){
        addContentView(getContentView()==null?0:getContentView().size(),view);
    }

    public void addTitleView(View view){
        addTitleView(getTitleView()==null?0:getTitleView().size(),view);
    }

    public void addTopBackgroundView(View view){
        addTopBackgroundView(getTopBackgroundView()==null?0:getTopBackgroundView().size(),view);
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }
    public void setTopBackgroundDark(boolean topBackgroundDark) {
        this.topBackgroundDark = topBackgroundDark;
    }

    public boolean isTitleViewIsHover() {
        return titleViewIsHover;
    }

    public void setTitleViewIsHover(boolean titleViewIsHover) {
        this.titleViewIsHover = titleViewIsHover;
    }

    public View getDividerView() {
        return dividerView;
    }
    public float getEndOffsetY() {
        return endOffsetY;
    }
    public void setDividerView(View dividerView) {
        this.dividerView = dividerView;
    }

    public List<View> getTitleView() {
        return titleView;
    }

    public void setTitleView(List<View> titleView) {
        this.titleView = titleView;
    }

    public List<View> getTopBackgroundView() {
        return topBackgroundView;
    }

    public void setTopBackgroundView(List<View> topBackgroundView) {
        this.topBackgroundView = topBackgroundView;
    }

    public List<View> getContentView() {
        return contentView;
    }

    public void setContentView(List<View> contentView) {
        this.contentView = contentView;
    }

    public CustomSwipeRefreshLayout getMoreFunRefreshLayout() {
        return moreFunRefreshLayout;
    }

    public CustomSwipeRefreshLayout.OnRefreshOnLoadListener getOnRefreshOnLoadListener() {
        return onRefreshOnLoadListener;
    }

    public void setOnRefreshOnLoadListener(CustomSwipeRefreshLayout.OnRefreshOnLoadListener onRefreshOnLoadListener) {
        this.onRefreshOnLoadListener = onRefreshOnLoadListener;
        if (getMoreFunRefreshLayout()!=null)
            getMoreFunRefreshLayout().setOnRefreshOnLoadListener(onRefreshOnLoadListener);
    }

    public float getTopPaddingNoBG() {
        return topPaddingNoBG;
    }

    public void setTopPaddingNoBG(float topPaddingNoBG) {
        this.topPaddingNoBG = topPaddingNoBG;
        postInvalidate();
    }

    @Override
    public void onScroll(float offset) {

    }


    @Override
    public void onViewSizeChange(View view, float changeSize) {
        if (!isByLoactionViewScale)
            changeTopBackgroundView(changeSize);
    }

    @Override
    public void onViewAttached(RecyclerView.ViewHolder viewHolder, int position) {
        if (getViews()==null
                ||getViews().size()<=position+1)
            return;
    }

    @Override
    public void onViewDetached(RecyclerView.ViewHolder viewHolder, int position) {
        if (getViews()==null
                ||getViews().size()<=position+1
                )
            return;
        View view = getViews().get(position+1);
        if (getTitleView().contains(view)){//表示下一个是标题视图，并且没有添加到标题视图中
            if (view.getParent() instanceof ViewGroup
                    &&moreFunTitleViewContentLL.indexOfChild(view)==-1){
                Log.d(TAG, "onViewDetached: "+view.getParent());

                ((ViewGroup) view.getParent()).removeView(view);
                moreFunTitleViewContentLL.addView(view);
            }
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (moreFunRecycleView!=null)
            setTitleViewContentLLTop(top+moreFunRecycleView.getPaddingTop());
    }

    /**
     * 设置标题视图的顶部边距
     * @param top
     */
    private void setTitleViewContentLLTop(int top){
        RelativeLayout.LayoutParams layoutParams;
        if (moreFunTitleViewContentLL.getLayoutParams() == null) {
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT
                    ,RelativeLayout.LayoutParams.WRAP_CONTENT
            );
        }else{
            layoutParams = (RelativeLayout.LayoutParams) moreFunTitleViewContentLL.getLayoutParams();
        }
        layoutParams.topMargin = (top);
        if (moreFunTitleViewContentLL!=null) {
            moreFunTitleViewContentLL.setLayoutParams(layoutParams);
        }
    }
    View tempView;
    @Override
    public void onVisibilityChanged(View view,boolean isVisibility) {
        Log.d(TAG, "onVisibilityChanged: "+isVisibility);
        if (isVisibility) {
            //返回到列表中
            tempView = view;
//            MoreFunListAdapter.MoreFunViewHolder viewHolder = (MoreFunListAdapter.MoreFunViewHolder) tempView.getTag();
//            if (viewHolder instanceof MoreFunListAdapter.MoreFunViewHolder
//                    && tempView instanceof MoreFunViewGrop) {
//                if (((ViewGroup) viewHolder.itemView).indexOfChild(views.get(viewHolder.getAdapterPosition())) == -1) {//表示该视图不存在在viewholder中了
//                    (viewHolder).addView(views.get(viewHolder.getAdapterPosition()), MoreFunListView.this);
//                }
//            }
        }else{
            tempView = view;

            MoreFunListAdapter.MoreFunViewHolder viewHolder = (MoreFunListAdapter.MoreFunViewHolder) view.getTag();
            View childView = getViews().get(viewHolder.getAdapterPosition());
            if (getTitleView().contains(childView)){//表示下一个是标题视图
                if (moreFunTitleViewContentLL.indexOfChild(childView)==-1)
                    return;

                if (childView.getParent() instanceof ViewGroup){
                    ((ViewGroup) childView.getParent()).removeView(childView);
                    moreFunTitleViewContentLL.addView(childView);
                }
            }
        }
    }


    public static class MoreFunLayoutParams extends LayoutParams{
        private int viewType;
        private int titleIndex;//标题的缩影

        public MoreFunLayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            initParams(c,attrs);
        }

        private void initParams(@NonNull Context c, @Nullable AttributeSet attrs){
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.MoreFunListView);
            viewType = typedArray.getInt(R.styleable.MoreFunListView_viewType,-1);
            titleIndex = typedArray.getInt(R.styleable.MoreFunListView_titleIndex,0);

            typedArray.recycle();
        }

        public int getTitleIndex() {
            return titleIndex;
        }

        public void setTitleIndex(int titleIndex) {
            this.titleIndex = titleIndex;
        }

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }
    }
}
