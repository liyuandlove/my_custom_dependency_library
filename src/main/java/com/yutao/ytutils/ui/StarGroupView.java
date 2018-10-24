package com.yutao.ytutils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;

import java.text.NumberFormat;

/**
 * a：余涛
 * b：1054868047
 * c：2018/9/6 10:00
 * d：小星星组控件
 * 支持 设置 是否显示 空心星星，支持自定义星星图片，支持当做进度条使用
 */
public class StarGroupView extends View implements View.OnClickListener {
    private Bitmap starBitmap//小星星的图案
            ,starFullBitmap//小星星完整的时候的图案
            ,progressBitmap
            ;
    private float starWidth //小星星的宽高
            ,starHeight
            ;
    private float starMargin//小星星之间的间距
            ;
    private int starNumber//小星星的个数
            ;
    private int canvasWidth//画布的宽高
            ,canvasHeight
            ;
    private int viewWidth
            ,viewHeight
            ;
    private float maxProgress //进度以及最大进度
            ,progress
            ;

    private Paint mPaint;

    private Matrix matrix;

//    private PorterDuffXfermode[] porterDuffXfermodes = new PorterDuffXfermode[]{
//            new PorterDuffXfermode(PorterDuff.Mode. CLEAR)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SRC)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DST)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DST_IN)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
//            ,new PorterDuffXfermode(PorterDuff.Mode.XOR)
//            ,new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
//            ,new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
//            ,new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
//            ,new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
//            ,new PorterDuffXfermode(PorterDuff.Mode.ADD)
//            ,new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
//    };
//    private PorterDuffXfermode porterDuffXfermode = porterDuffXfermodes[4];

    private boolean isShowHollow = true;//是否展示空心小星星
    private float starValue = 0;//小星星值
    private OnStarClickListener onStarClickListener;

    private boolean isShowHalfStar = false;
    private boolean isOnlyInt = false;//是否只展示整个小星星

    private float x;
    private float y;

    private NumberFormat numberFormat = NumberFormat.getInstance();

    public StarGroupView(Context context) {
        super(context);
        initViews();
    }

    public StarGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public StarGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(){
        initViews(null);
    }

    private void initDatas(AttributeSet attrs){
        if (attrs!=null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.StarGroupView);
            starWidth = array.getDimension(R.styleable.StarGroupView_starWidth,PhoneUtils.dpTopx(getContext(),15));
            starHeight = array.getDimension(R.styleable.StarGroupView_starHeight, PhoneUtils.dpTopx(getContext(),15));
            starMargin =  array.getDimension(R.styleable.StarGroupView_starMargin,PhoneUtils.dpTopx(getContext(),2.5f));
            starNumber = array.getInteger(R.styleable.StarGroupView_starNumber,5);

            starBitmap = BitmapFactory.decodeResource(getResources(),array.getResourceId(R.styleable.StarGroupView_starEmptyBitmap, R.mipmap.icon_star));
            starFullBitmap = BitmapFactory.decodeResource(getResources(),array.getResourceId(R.styleable.StarGroupView_starFullBitmap, R.mipmap.icon_star_full));
            setStarBitmap(starBitmap);
            setStarFullBitmap(starFullBitmap);

            maxProgress = array.getInteger(R.styleable.StarGroupView_maxProgress,100);
            progress = array.getInteger(R.styleable.StarGroupView_maxProgress,0);

            starValue = array.getFloat(R.styleable.StarGroupView_starValue,0);
            isShowHollow = array.getBoolean(R.styleable.StarGroupView_isShowHollow,true);

            isShowHalfStar = array.getBoolean(R.styleable.StarGroupView_isShowHalfStar,false);
            isOnlyInt = array.getBoolean(R.styleable.StarGroupView_isOnlyInt,false);

            if (progress!=0)
                setProgress(progress);
            if (starValue!=0)
                setStarValue(starValue);

            array.recycle();
        }else{
            starWidth = (int) PhoneUtils.dpTopx(getContext(),15);
            starHeight = starWidth;
            starMargin = (int) PhoneUtils.dpTopx(getContext(),2.5f);
            starNumber = 5;
            starBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_star);
            starFullBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_star_full);
            setStarBitmap(starBitmap);
            setStarFullBitmap(starFullBitmap);
            maxProgress = 100f;
            progress = 75f;
        }
    }

    private void initViews(AttributeSet attrs){
        initDatas(attrs);

        initPaint();

        setOnClickListener(this);
    }

    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setTextSize(20);
        mPaint.setStrokeWidth(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int canvasWidth = (int) ((starNumber*2) * starMargin + starNumber*starWidth);
        int canvasHeight = (int) starHeight;

        setCanvasWidth(canvasWidth);
        setCanvasHeight(canvasHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setViewHeight(h);
        setViewWidth(w);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG));

        drawStars(canvas);
        drawProgress(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (starBitmap!=null){
            starBitmap.recycle();
            starBitmap = null;
        }
        if (starFullBitmap!=null){
            starFullBitmap.recycle();
            starFullBitmap = null;
        }
        if (progressBitmap!=null){
            progressBitmap.recycle();
            progressBitmap = null;
        }
    }

    /**
     * 绘制小星星
     * @param canvas
     */
    private void drawStars(Canvas canvas){
        if (starBitmap == null
                ||getViewWidth() <= 0
                ||getViewHeight() <= 0)
            return;
        mPaint.setStyle(Paint.Style.FILL);
        Rect rect = new Rect();
        rect.left = (getViewWidth()-getCanvasWidth())/2;
        rect.right = rect.left + getCanvasWidth();
        rect.top = 0;
        rect.bottom = getCanvasHeight();

        for(int i= 0;i<starNumber;i++){
            canvas.drawBitmap(starBitmap,
                    rect.left+
                            i*(getStarWidth()+getStarMargin()*2)+getStarMargin(),0,mPaint);
        }
    }

    /**
     * 绘制进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        if (starFullBitmap == null
                ||progress <= 0
                ||getViewHeight() <= 0
                ||getCanvasWidth() <= 0
                ||maxProgress <= 0
                ||(int) (getCanvasWidth()*progress/maxProgress)<=0
                )
            return;
        mPaint.setStyle(Paint.Style.FILL);

        if (progressBitmap!=null){
            progressBitmap.recycle();
            progressBitmap = null;
        }

        progressBitmap = Bitmap.createBitmap((int) (getCanvasWidth()*progress/maxProgress), getViewHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(progressBitmap);
//        Rect rect = new Rect();
//        rect.left = (getViewWidth()-getCanvasWidth())/2;
//        rect.top = 0;
//        rect.bottom = getViewHeight();
//        rect.right = rect.left+(int) (getCanvasWidth()*progress/maxProgress);

        for(int i= 0;i<starNumber;i++){
            tempCanvas.drawBitmap(starFullBitmap,
                    i*(getStarWidth()+getStarMargin()*2)+getStarMargin(),0,mPaint);
        }

        canvas.drawBitmap(progressBitmap,(getViewWidth()-getCanvasWidth())/2,0,mPaint);
    }

    public int getViewWidth() {
        if (viewWidth == 0)
            viewWidth = getWidth();
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        if (viewHeight == 0)
            viewHeight = getHeight();
        return viewHeight;
    }

    public boolean isOnlyInt() {
        return isOnlyInt;
    }

    public void setOnlyInt(boolean onlyInt) {
        isOnlyInt = onlyInt;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public void setStarBitmap(Bitmap starBitmap) {
        matrix = new Matrix();
        float scaleX = Float.valueOf(starWidth)/Float.valueOf(starBitmap.getWidth());
        float scaleY = Float.valueOf(starHeight)/Float.valueOf(starBitmap.getHeight());
        matrix.postScale(scaleX,scaleY);
        starBitmap = Bitmap.createBitmap(starBitmap,0,0,starBitmap.getWidth(),starBitmap.getHeight(),matrix,true);
        this.starBitmap = starBitmap;
    }

    public Bitmap getStarFullBitmap() {
        return starFullBitmap;
    }

    public void setStarFullBitmap(Bitmap starFullBitmap) {
        matrix = new Matrix();
        float scaleX = Float.valueOf(starWidth)/Float.valueOf(starFullBitmap.getWidth());
        float scaleY = Float.valueOf(starHeight)/Float.valueOf(starFullBitmap.getHeight());
        matrix.postScale(scaleX,scaleY);
        starFullBitmap = Bitmap.createBitmap(starFullBitmap,0,0,starFullBitmap.getWidth(),starFullBitmap.getHeight(),matrix,true);
        this.starFullBitmap = starFullBitmap;
    }

    public void setStarWidth(int starWidth) {
        this.starWidth = starWidth;
    }

    public void setStarHeight(int starHeight) {
        this.starHeight = starHeight;
    }

    public float getStarValue() {
        return starValue;
    }

    public void setStarValue(float starValue) {
        setStarValue(starValue,isShowHalfStar);
    }

    public void setStarValue(float starValue,boolean isShowHalfStar){
        setStarValue(starValue,isShowHalfStar,isOnlyInt);
    }

    public void setStarValue(float starValue,boolean isShowHalfStar,boolean isOnlyInt){
        if (starValue > getStarNumber())
            starValue = getStarNumber();
        if (starValue < 0)
            starValue = 0;
        if (isShowHalfStar){//如果只展示半个星星
            int starIntNumber = (int) starValue;
            if (starValue-starIntNumber!=0) {
                if ((starValue - starIntNumber) > 0.5f) {
                    starValue = starIntNumber + 1;
                } else {
                    starValue = starIntNumber + 0.5f;
                }
            }
        }
        if (isOnlyInt){//如果只展示整个星星
//            starValue = Math.round(starValue);//四舍五入的方式
            if (starValue<=0)
                starValue = 0;
            else
                starValue = (int)starValue+1;
        }

        this.starValue = starValue;
        if (!isShowHollow){//如果不展示空心小星星
            int starNum = (int) starValue;
            if (starValue%1 != 0)
                starNum = starNum+1;
            setStarNumber(starNum);
        }
        setProgress(maxProgress*starValue/starNumber);
    }

    public Bitmap getStarBitmap() {
        return starBitmap;
    }

    public float getStarMargin() {
        return starMargin;
    }

    public int getStarNumber() {
        return starNumber;
    }

    public float getStarWidth() {
        return starWidth;
    }

    public float getStarHeight() {
        return starHeight;
    }

    public void setStarMargin(int starMargin) {
        this.starMargin = starMargin;
    }

    public void setStarNumber(int starNumber) {
        this.starNumber = starNumber;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (progress<0)
            progress = 0;
        this.progress = progress;
        postInvalidate();
    }

    public void setOnStarClickListener(OnStarClickListener onStarClickListener) {
        this.onStarClickListener = onStarClickListener;
    }
//    public void setPorterDuffXfermode(PorterDuffXfermode porterDuffXfermode) {
//        this.porterDuffXfermode = porterDuffXfermode;
//        postInvalidate();
//    }

//    private int position = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onStarClickListener == null
//                ||!isClickOnStar(event)//TODO 这个方法可以避免点击到外部造成回调
                )
            return super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                setScrollStar(event.getX(),event.getY(),true);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setScrollStar(event.getX(),event.getY(),false);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否点击到了星星上
     * @return
     */
    private boolean isClickOnStar(MotionEvent event){
        if (event == null)
            return false;
        if (event.getX()<getViewWidth()/2+getCanvasWidth()/2-getStarMargin()
                &&event.getX()>getViewWidth()/2-getCanvasWidth()/2+getStarMargin()){
            if (event.getY()<getCanvasHeight()){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是点击事件
     * @param nowX
     * @param nowY
     * @return
     */
    private boolean isClickEvent(float nowX,float nowY){
        int slopTouch = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        boolean isClick = Math.pow(nowX-x,2)+Math.pow(nowY-y,2)<Math.pow(slopTouch,2);
        if (!isClick){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return isClick;
    }

    /**
     * 设置滚动星星
     * @param nowX
     * @param nowY
     * @param isScroll
     * @return
     */
    private void setScrollStar(float nowX,float nowY,boolean isScroll){
        if (isScroll){
            if (isClickEvent(nowX,nowY))
                return;
        }else{
            if (!isClickEvent(nowX,nowY)) {
                if (isShowHalfStar||isOnlyInt)
                    setProgressByX(nowX,isScroll);
                return;
            }
        }
        setProgressByX(nowX,isScroll);
    }

    /**
     * 根据x轴坐标设置进度
     * @param nowX
     */
    private void setProgressByX(float nowX,boolean isScroll){
        float d = nowX - (getViewWidth()-getCanvasWidth())/2;
        float dr = d/getCanvasWidth();
        if (dr>1)
            dr = 1;
//        float progress = dr*maxProgress;
        float starValue = dr * getStarNumber();

        setStarValue(starValue,isShowHalfStar&&!isScroll,isOnlyInt&&!isScroll);

//        setProgress(progress);

        if(onStarClickListener!=null){
            if(isScroll){
                onStarClickListener.onStarScoll(this, (int) getStarValue(),getStarValue(),getProgress());
            }else {
                onStarClickListener.onStarClick(this, (int) getStarValue(),getStarValue(),getProgress());
            }
        }
    }

    @Override
    public void onClick(View v) {

        //TODO 测试方法，用来测试显示混合模式的，后期可以加设置星星位置

//        if (position>=porterDuffXfermodes.length)
//            position = 0;
//
//        setPorterDuffXfermode(porterDuffXfermodes[position++]);
//
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (position<100)
//                    setProgress(position++);
//                else
//                    position = 0;
//                handler.postDelayed(this,16);
//            }
//        };
//
//        handler.postDelayed(runnable,250);

//            ToastUtils.getInstance().showMessageToast("----"+i+"----");
    }

    public interface OnStarClickListener{
        void onStarClick(StarGroupView view, int starNumInt, float starNumFloat, float progress);
        void onStarScoll(StarGroupView view, int starNumInt, float starNumFloat, float progress);
    }
}
