package com.yutao.ytutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;


/**
 * 图片加载
 * Created by Administrator on 2017/5/10 0010.
 */

public class GlideUtils {

    public static RequestOptions getOptions() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }

    public static RequestOptions getCricleOptions() {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }

    public static RequestOptions getOptionsHaveAnimate() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }
    public static RequestOptions getOptionsCenterInside() {
        RequestOptions options = new RequestOptions()
                .centerInside()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }
    public static RequestOptions getOptionsCenterInsideOverride(int width,int height) {
        RequestOptions options = new RequestOptions()
                .centerInside()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH)
                .override(width,height)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }


    /**
     * 获得加载圆角图片的配置
     * @param mContext
     * @param radius
     * @return
     */
    public static RequestOptions getRoundRequestOptions(Context mContext,int radius,int placeholder){
        RequestOptions  requestOptions = new RequestOptions()
                .transform(GlideUtils.getGlideRoundTransform(mContext,radius))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .priority(Priority.HIGH);
        return requestOptions;
    }
    public static RequestOptions getRoundRequestOptions(Context mContext,int radius){
        RequestOptions  requestOptions = new RequestOptions()
                .transform(GlideUtils.getGlideRoundTransform(mContext,radius))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.color.color_f7f7f7)
                .error(R.color.color_f7f7f7)
                .priority(Priority.HIGH);
        return requestOptions;
    }


    /**
     * 获得加载圆角的工具
     * @param mContext
     * @return
     */
    public static GlideRoundTransform getGlideRoundTransform(Context mContext) {
        GlideRoundTransform glideRoundTransform=new GlideRoundTransform(mContext);
        return glideRoundTransform;
    }
    /**
     * 获得加载圆角的工具
     * @param mContext
     * @return
     */
    public static GlideRoundTransform getGlideRoundTransform(Context mContext,int radio) {
        GlideRoundTransform glideRoundTransform=new GlideRoundTransform(mContext,radio);
        return glideRoundTransform;
    }
    /**
     * 加载圆角图片的工具
     */
    public static class GlideRoundTransform extends BitmapTransformation {
        private static int radius = 5;
        private Context mContext;
        public GlideRoundTransform(Context mContext) {
            super();
            this.mContext=mContext;
        }
        public GlideRoundTransform(Context mContext,int radiusTemp) {
            super();
            radius=radiusTemp;
            this.mContext=mContext;
        }
        @Override
        public Bitmap transform(BitmapPool pool, Bitmap toTransform,
                                int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            int temp=Math.min(source.getWidth(),source.getHeight());
            RectF rectF = new RectF(0f, 0f, temp, temp);
            canvas.drawRoundRect(rectF, PhoneUtils.dpTopx(mContext,radius), PhoneUtils.dpTopx(mContext,radius), paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }
    }


    public static RequestOptions getOptionsImg() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .priority(Priority.HIGH)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return options;
    }
}
