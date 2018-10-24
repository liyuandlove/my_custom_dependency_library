package com.yutao.ytutils.ui.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;


/**
 * 自动换行
 */

public class CustomRelativeLayout extends RelativeLayout {
    private String TAG=CustomRelativeLayout.class.getSimpleName();
    private int paddingRight=10,paddingBottom=10;
    private Context mContext;


    public CustomRelativeLayout(Context context) {
        super(context);
        initViews(context,null);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context,attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context,attrs);
    }

    private void initViews(Context mContext,AttributeSet attrs){
        this.mContext=mContext;
//        paddingRight = (int) PhoneUtils.dpTopx(getContext(), 10);
//        paddingBottom = (int) PhoneUtils.dpTopx(getContext(), 10);
        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.CustomRelativeLayout);
            paddingRight = (int) typedArray.getDimension(R.styleable.CustomRelativeLayout_insideMarginRight,PhoneUtils.dpTopx(getContext(), paddingRight));
            paddingBottom  = (int) typedArray.getDimension(R.styleable.CustomRelativeLayout_insideMarginBottom,PhoneUtils.dpTopx(getContext(), paddingBottom));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width=getWidth();
        int height=getHeight();//获得父布局的总宽高

        int viewOneLineWidth=0;//一行的宽度

        int lineHeight=0;//记录行高

        for (int i=0;i<getChildCount();i++){
            View child=getChildAt(i);
            int childWidth=child.getMeasuredWidth();
            int childHeight=child.getMeasuredHeight();//获得子View的宽高
            MarginLayoutParams childMarginLayoutParams= (MarginLayoutParams) child.getLayoutParams();

//            childMarginLayoutParams.leftMargin=viewOneLineWidth;

            if (viewOneLineWidth+childWidth+childMarginLayoutParams.leftMargin+childMarginLayoutParams.rightMargin//算出总共的大小,如果该宽度大于父视图宽度，则需要换行
                    >width){
//                childMarginLayoutParams.topMargin=lineHeight;
                viewOneLineWidth=0;

                lineHeight= (int) (lineHeight+childHeight+childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin+ paddingBottom);//保存最大宽度
            }

            child.layout(viewOneLineWidth,lineHeight
                    ,viewOneLineWidth+childWidth+childMarginLayoutParams.leftMargin+childMarginLayoutParams.rightMargin
                    ,lineHeight+childHeight+childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin);

            viewOneLineWidth= (int) (viewOneLineWidth+childWidth+childMarginLayoutParams.leftMargin+childMarginLayoutParams.rightMargin+paddingRight);//保存宽度


//            setMeasuredDimension(width
//                    ,lineHeight+childHeight+childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin
//            );
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int heightOfNew=0;

        int width=MeasureSpec.getSize(widthMeasureSpec);

        int viewOneLineWidth=0;//一行的宽度

        int lineHeight=0;//记录行高

        for (int i=0;i<getChildCount();i++){
            View child=getChildAt(i);
            int childWidth=child.getMeasuredWidth();
            int childHeight=child.getMeasuredHeight();//获得子View的宽高
            MarginLayoutParams childMarginLayoutParams= (MarginLayoutParams) child.getLayoutParams();

            if (viewOneLineWidth+childWidth+childMarginLayoutParams.leftMargin+childMarginLayoutParams.rightMargin//算出总共的大小,如果该宽度大于父视图宽度，则需要换行
                    >width){
                viewOneLineWidth=0;

                lineHeight= (int) (lineHeight+childHeight+childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin + paddingBottom);//保存最大宽度
            }


            viewOneLineWidth= (int) (viewOneLineWidth+childWidth+childMarginLayoutParams.leftMargin+childMarginLayoutParams.rightMargin + paddingRight);//保存宽度


            heightOfNew=lineHeight+childHeight+childMarginLayoutParams.topMargin+childMarginLayoutParams.bottomMargin;
            Log.d(TAG, "onMeasure: "+heightOfNew+" "+lineHeight);
        }


        setMeasuredDimension(width,heightOfNew);

//        /**
//         * 获得此ViewGroup上级容器为其推荐的宽和高，以及记算模式
//         */
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
//        /**
//         * 记录如果是wrap_content是设置的宽和高
//         */
//        int width = 0;
//        int height = 0;
//
//        int cCount = getChildCount();
//
//        int cWidth = 0;
//        int cHeight = 0;
//        MarginLayoutParams cParams = null;
//
//        // 用于记算左边两个childView的高度
//        int lHeight = 0;
//        // 用于记算右边两个childView的高度，最终高度取二者之间大值
//        int rHeight = 0;
//
//        // 用于记算上边两个childView的宽度
//        int tWidth = 0;
//        // 用于记算下面两个childiew的宽度，最终宽度取二者之间大值
//        int bWidth = 0;
//        for (int i = 0; i < cCount; i++)
//        {
//            View childView = getChildAt(i);
//            cWidth = childView.getMeasuredWidth();
//            cHeight = childView.getMeasuredHeight();
//            cParams = (MarginLayoutParams) childView.getLayoutParams();
//
//            // 上面两个childView
//            if (i == 0 || i == 1)
//            {
//                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
//            }
//
//            if (i == 2 || i == 3)
//            {
//                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
//            }
//
//            if (i == 0 || i == 2)
//            {
//                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
//            }
//
//            if (i == 1 || i == 3)
//            {
//                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
//            }
//
//        }
//
//        width = Math.max(tWidth, bWidth);
//        height = Math.max(lHeight, rHeight);
//
//        /**
//         * 如果是wrap_content设置为我们记算的值
//         * 否则：直接设置为父容器记算的值
//         */
//        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
//                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
//                : height);
    }
}
