package com.yutao.ytutils.ui.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;

import java.util.List;

/**
 * a：余涛
 * b：1054868047
 * c：2018/9/7 14:40
 * d：测试视图阴影跟随视图主色调改变，可以设置
 *      阴影半径
 *      圆角半径
 *      是否是圆形
 *      阴影颜色                    （必须设置是否阴影颜色根据背景改变为false，自定义的阴影颜色才能生效）
 *      阴影透明值
 *      是否是填满中间               （此项存在问题，如果不填满中间，会默认绘制10dp的外框）
 *      是否阴影的颜色根据背景改变
 */
public class ShadowViewGroup extends FrameLayout {

    private int viewWith
            ,viewHeight
            ;
    private Paint paint4 = new Paint();
    private Path path = new Path();
    private float shawdowRadio ;//阴影半径
    private float roundRadio;//圆角半径
    private boolean isCricle;//是否是圆形
    private int shawdowColor;//阴影颜色
    private int shawdowAlhpa;//阴影透明值
    private boolean isFill;//是否是填满中间
    private boolean isShawdowColorChange;//是否阴影的颜色根据背景改变

    public ShadowViewGroup(@NonNull Context context) {
        super(context);
        initViews(null);
    }

    public ShadowViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public ShadowViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs){
        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.ShadowViewGroup);
            shawdowRadio = typedArray.getDimension(R.styleable.ShadowViewGroup_shawdowRadio,PhoneUtils.dpTopx(getContext(), 10));
            roundRadio = typedArray.getDimension(R.styleable.ShadowViewGroup_roundRadio,PhoneUtils.dpTopx(getContext(), 10));
            isCricle = typedArray.getBoolean(R.styleable.ShadowViewGroup_isCricle,true);
            isFill = typedArray.getBoolean(R.styleable.ShadowViewGroup_isFill,true);
            shawdowColor = typedArray.getColor(R.styleable.ShadowViewGroup_shawdowColor,Color.BLACK);
            shawdowAlhpa = typedArray.getInt(R.styleable.ShadowViewGroup_shawdowAlhpa,255);
            isShawdowColorChange = typedArray.getBoolean(R.styleable.ShadowViewGroup_isShawdowColorChange,true);
            typedArray.recycle();
        }else {
            shawdowRadio = PhoneUtils.dpTopx(getContext(), 20);
            roundRadio = PhoneUtils.dpTopx(getContext(), 10);
            isCricle = true;
        }
        setWillNotDraw(false);//设置viewgroup进入ondraw方法
        int defaultPadding = (int) shawdowRadio;
        setPadding(defaultPadding,defaultPadding,defaultPadding,defaultPadding);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWith = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE,null);

        paint4.reset();
        paint4.setAntiAlias(true);
        paint4.setDither(true);
        if (!isShawdowColorChange
                &&shawdowColor == Color.BLACK) {//如果没有阴影改变，则设置颜色为纯黑
            paint4.setColor(Color.BLACK);
            paint4.setAlpha((int) (255 * 0.32f));
            paint4.setShadowLayer(shawdowRadio, 0, 0, Color.BLACK);
        }else{
            paint4.setColor(shawdowColor);
            paint4.setAlpha(shawdowAlhpa);
            paint4.setShadowLayer(shawdowRadio, 0, 0, shawdowColor);
        }
        if (isFill) {
            paint4.setStyle(Paint.Style.FILL);
            paint4.setStrokeWidth(0);
        }else {
            paint4.setStyle(Paint.Style.STROKE);
            paint4.setStrokeWidth(10);
        }
        path.reset();

        if (isCricle){
            canvas.drawCircle(viewWith/2,viewHeight/2,viewWith/2-shawdowRadio,paint4);
            path.addCircle(viewWith/2,viewHeight/2,viewWith/2, Path.Direction.CW);
        }else if (roundRadio==0){
            RectF rectF = new RectF(shawdowRadio,shawdowRadio,viewHeight-shawdowRadio,viewWith-shawdowRadio);
            canvas.drawRect(rectF,paint4);
            path.addRect(0,0,viewWith,viewHeight, Path.Direction.CW);
        }else{
            RectF rectF = new RectF(shawdowRadio,shawdowRadio,viewHeight-shawdowRadio,viewWith-shawdowRadio);
            canvas.drawRoundRect(rectF,shawdowRadio,shawdowRadio,paint4);
            path.addRect(0,0,viewWith,viewHeight, Path.Direction.CW);
        }
        super.onDraw(canvas);
        canvas.clipPath(path);
    }

    public void setShawdowColor(int shawdowColor) {
        this.shawdowColor = shawdowColor;
        postInvalidate();
    }

    private void colorChange(Color srcColor){
        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.set(new float[]{srcColor.red(),srcColor.green(),srcColor.blue()});
//        colorMatrix.set(0,srcColor.red());
//        colorMatrix.set(1,srcColor.green());
//        colorMatrix.set(2,srcColor.blue());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!isShawdowColorChange) {
            return;
        }

        getChildAt(0).destroyDrawingCache();
        getChildAt(0).setDrawingCacheEnabled(false);
        getChildAt(0).setDrawingCacheEnabled(true);

        final Bitmap viewBitmap = getChildAt(0).getDrawingCache();
//        Palette.Swatch s = p.getVibrantSwatch();       //获取到充满活力的这种色调
//        Palette.Swatch s = p.getDarkVibrantSwatch();    //获取充满活力的黑
//        Palette.Swatch s = p.getLightVibrantSwatch();   //获取充满活力的亮
//        Palette.Swatch s = p.getMutedSwatch();           //获取柔和的色调
//        Palette.Swatch s = p.getDarkMutedSwatch();      //获取柔和的黑
//        Palette.Swatch s = p.getLightMutedSwatch();    //获取柔和的亮
//        getPopulation(): 像素的数量
//        getRgb(): RGB颜色
//        getHsl(): HSL颜色
//        getBodyTextColor(): 用于内容文本的颜色
//        getTitleTextColor(): 标题文本的颜色

        setBackgroundColor(Color.TRANSPARENT);

        if (viewBitmap!=null){
            Palette.from(viewBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@NonNull Palette palette) {
                    int mainColor = -1;
                    List<Palette.Swatch> swatches = palette.getSwatches();

                    Palette.Swatch[] swatcheArray = new Palette.Swatch[]{
                            palette.getVibrantSwatch()
                            ,palette.getDarkVibrantSwatch()
                            ,palette.getLightVibrantSwatch()
                            ,palette.getMutedSwatch()
                            ,palette.getDarkMutedSwatch()
                            ,palette.getLightMutedSwatch()
                            ,palette.getDominantSwatch()
                    };
//                    Log.d("---------", "onGenerated: "+(swatches==null?null:swatches.size())+" "+swatcheArray.length);

                    int pxSize = 0;
                    if (swatches!=null){
                        for (Palette.Swatch swatch :swatches){
                            if (swatch!=null
                                    &&pxSize<swatch.getPopulation()){
                                pxSize = swatch.getPopulation();
                                mainColor = swatch.getRgb();
                            }
                        }
                    }else{
                        for (Palette.Swatch swatch :swatcheArray){
                            if (swatch!=null
                                    &&pxSize<swatch.getPopulation()){
                                pxSize = swatch.getPopulation();
                                mainColor = swatch.getRgb();
                            }
                        }
                    }
                    setShawdowColor(mainColor);
                    viewBitmap.recycle();
                }
            });
        }
    }
}
