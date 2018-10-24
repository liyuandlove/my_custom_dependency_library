package com.yutao.ytutils.ui.imageview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/22 10:00
 * d：自定义imageView，可以圆角，可以圆形
 */
public class YTImageView extends AppCompatImageView implements ViewTreeObserver.OnScrollChangedListener {
    private String TAG = "YTImageView";

    private boolean isCricle = false;//是否是圆形

    private boolean isRound = true;//是否是圆角
    private float roundSize = -1//圆角大小
            ,roundTopLeft = -1//顶部左边圆角
            ,roundTopRight = -1//顶部右边圆角
            ,roundBottomLeft = -1//底部左边圆角
            ,roundBottomRight = -1//底部右边圆角
            ;

    private Paint mClipPaint;//剪切画笔

    private Xfermode mXfermode;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.MULTIPLY;

    private int viewWidth,viewHeight;
    private RectF dstRect, srcRect;

    private Bitmap dstBitap
            ,srcBmp;

    private Canvas clipCanvas;
    private Paint bitmapPaint;//临时画笔
    private BitmapDrawable bD;
    private int saveCount;

    private int[] screenLocation = new int[2];

    public YTImageView(Context context) {
        super(context);
        initDatas(null);
    }

    public YTImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDatas(attrs);
    }

    public YTImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDatas(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationOnScreen(screenLocation);
        Log.d(TAG, "onLayout: "+screenLocation[1]);
    }
    boolean isHeightWrap = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        isHeightWrap = false;

        if (heightMode == MeasureSpec.AT_MOST) {
            Log.d(TAG, "onMeasure: AT_MOST");

        }else if (heightMode == MeasureSpec.EXACTLY){
            Log.d(TAG, "onMeasure: EXACTLY");

        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            Log.d(TAG, "onMeasure: UNSPECIFIED");
            isHeightWrap = true;

            if (getDrawable() != null
                    &&getDrawable() instanceof BitmapDrawable) {//自动适配图片
                bD = (BitmapDrawable) getDrawable();
                dstBitap = bD.getBitmap();
                int bitmapHeight = dstBitap.getHeight();
                int bitmapWidth = dstBitap.getWidth();

                if (bitmapHeight == 0) {
                    bitmapHeight = height;
                }else if (isHeightWrap){
                    bitmapHeight = width*bitmapHeight/bitmapWidth;
//                    Log.d(TAG, "onSizeChanged: "+bitmapHeight+" "+viewHeight+" "+height+" "+width);
                }

                bitmapWidth = width;
                setMeasuredDimension(bitmapWidth,bitmapHeight);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        canvas.drawColor(Color.TRANSPARENT);

        boolean isDraw = isNeedDraw(canvas);

        if (!isDraw)//如果不绘制就不执行接下来的代码
            return;

        if (isCricle)//绘制圆形
            drawCricle(canvas);

        if (isRound)//绘制圆角
            drawRound(canvas,roundSize);

        if (!isCricle&&!isRound){//如果不是圆形也不是圆角，则直接执行本身的ondraw
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearBitmap();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initViews();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        srcRect = new RectF(0, 0, viewWidth, viewHeight);
        dstRect = new RectF(0, 0, viewWidth, viewHeight);
//        Log.d(TAG, "onSizeChanged: "+w+" "+h);
    }

    private void initViews(){
        mClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

        mXfermode = new PorterDuffXfermode(mPorterDuffMode);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        if (getParent() instanceof ViewGroup){
            ((ViewGroup) getParent()).getViewTreeObserver().addOnScrollChangedListener(this);
        }
    }

    private void initDatas(AttributeSet attrs) {

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.YTImageView);
            isCricle = typedArray.getBoolean(R.styleable.YTImageView_isCricleIV,false);
            if (isCricle) {
                isRound = typedArray.getBoolean(R.styleable.YTImageView_isRound, false);
            }else{
                isRound = typedArray.getBoolean(R.styleable.YTImageView_isRound, true);
            }
            roundSize = typedArray.getDimension(R.styleable.YTImageView_roundSize,PhoneUtils.dpTopx(getContext(),5));

            roundTopLeft = typedArray.getDimension(R.styleable.YTImageView_roundTopLeft,-1);
            roundTopRight = typedArray.getDimension(R.styleable.YTImageView_roundTopRight,-1);
            roundBottomLeft = typedArray.getDimension(R.styleable.YTImageView_roundBottomLeft,-1);
            roundBottomRight = typedArray.getDimension(R.styleable.YTImageView_roundBottomRight,-1);

            typedArray.recycle();
        }else{
            roundSize = PhoneUtils.dpTopx(getContext(),5);
        }
    }

    /**
     * 是否绘制
     * @param canvas
     * @return
     */
    private boolean isNeedDraw(Canvas canvas){
        if (getDrawable() == null
                &&!(getDrawable() instanceof BitmapDrawable))
            return false;
        //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
        saveCount = canvas.saveLayer(srcRect, mClipPaint, Canvas.ALL_SAVE_FLAG);

        bD = (BitmapDrawable) getDrawable();
        dstBitap = bD.getBitmap();

        srcBmp = Bitmap.createBitmap(viewWidth,viewHeight,Bitmap.Config.ARGB_8888);
        clipCanvas = new Canvas(srcBmp);
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        bitmapPaint.setColor(Color.WHITE);
        return true;
    }

    /**
     * 绘制源图
     * @param canvas
     */
    private void drawBitDraw(Canvas canvas){
//        super.onDraw(canvas);
        canvas.drawBitmap(dstBitap,null,dstRect,mClipPaint);
        mClipPaint.setXfermode(mXfermode);

        canvas.drawBitmap(srcBmp, null, srcRect, mClipPaint);
        mClipPaint.setXfermode(null);

        canvas.restoreToCount(saveCount);
    }

    /**
     * 绘制圆形
     * @param canvas
     */
    private void drawCricle(Canvas canvas){
        clipCanvas.drawCircle(viewWidth /2,viewHeight/2,viewHeight> viewWidth ? viewWidth /2:viewHeight/2,bitmapPaint);

        drawBitDraw(canvas);
    }

    /**
     * 绘制圆角
     * @param canvas
     * @param roundSize
     */
    private void drawRound(Canvas canvas,float roundSize) {
        if (roundTopLeft == -1)//默认-1未定义
            roundTopLeft = roundSize;
        if (roundTopRight == -1)
            roundTopRight = roundSize;
        if (roundBottomLeft == -1)
            roundBottomLeft = roundSize;
        if (roundBottomRight == -1)
            roundBottomRight = roundSize;
        Path roundPath = new Path();
        roundPath.moveTo(roundTopLeft,0);
        //float[] radii中有8个值，依次为左上角，右上角，右下角，左下角的rx,ry
        roundPath.addRoundRect(srcRect
                ,new float[]{roundTopLeft,roundTopLeft
                        ,roundTopRight,roundTopRight
                        ,roundBottomRight,roundBottomRight
                        ,roundBottomLeft,roundBottomLeft}
                ,Path.Direction.CCW);

        bitmapPaint.setStyle(Paint.Style.FILL);
        clipCanvas.drawPath(roundPath,bitmapPaint);

        drawBitDraw(canvas);
    }

    /**
     * 清理内存
     */
    private void clearBitmap() {
        if (getParent() instanceof ViewGroup){
            ((ViewGroup) getParent()).getViewTreeObserver().removeOnScrollChangedListener(this);
        }
        if (srcBmp != null){
            srcBmp.recycle();
            srcBmp = null;
        }
//        if (dstBitap != null){//这个让imageView自己回收
//            dstBitap.recycle();
//            dstBitap = null;
//        }

        mClipPaint = null;
        mXfermode = null;

        bD = null;
        clipCanvas = null;
        bitmapPaint = null;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        postInvalidate();
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        postInvalidate();
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    @Override
    public void onScrollChanged() {
        getLocationOnScreen(screenLocation);

        Rect visRect = new Rect();

        boolean isVisible = getGlobalVisibleRect(visRect);
        if (isVisible&&(viewHeight>visRect.height()
                || viewWidth >visRect.width())){//说明此时该view正在从屏幕可见区域中消失
//            Log.d(TAG, "onScrollChanged: ----->"+screenLocation[0]+" "+screenLocation[1]+" "+isVisible+" "+viewHeight+" "+visRect.height());

        }
    }
}
