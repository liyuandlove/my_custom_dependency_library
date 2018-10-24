package com.yutao.ytutils.ui.morefunlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * a：余涛
 * b：1054868047
 * c：2018/10/15 16:00
 * d：
 */
public class MoreFunListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<View> views;
    private List<View> titleViews;
    private OnViewHolderVisableChangeListener onViewHolderVisableChangeListener;
    private MoreFunTitleViewGrop.OnViewVisbilityChangeListener onViewVisbilityChangeListener;


    public MoreFunListAdapter(Context mContext, List<View> views,List<View> titleViews) {
        this.mContext = mContext;
        this.views = views;
        this.titleViews = titleViews;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ViewGroup relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        if (viewType == 1) {//表示为标题视图
            relativeLayout = new MoreFunTitleViewGrop(mContext);
        }

        MoreFunViewHolder moreFunViewHolder = new MoreFunViewHolder(relativeLayout);
        relativeLayout.setTag(moreFunViewHolder);

        return moreFunViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (onViewHolderVisableChangeListener != null)
            onViewHolderVisableChangeListener.onViewAttached(holder,holder.getAdapterPosition());
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (onViewHolderVisableChangeListener != null)
            onViewHolderVisableChangeListener.onViewDetached(holder,holder.getAdapterPosition());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MoreFunViewHolder){
            ((MoreFunViewHolder) viewHolder).addView(views.get(position),onViewVisbilityChangeListener);
        }
    }

    @Override
    public int getItemCount() {

        return views.size();
    }

    @Override
    public int getItemViewType(int position) {
//        MoreFunListView.MoreFunLayoutParams moreFunLayoutParams = null;
//        if (views.get(position).getLayoutParams() instanceof MoreFunListView.MoreFunLayoutParams) {
//            moreFunLayoutParams = (MoreFunListView.MoreFunLayoutParams) views.get(position).getLayoutParams();
//            if (moreFunLayoutParams != null){
//                Log.d("MoreFunListView", "addView: " + moreFunLayoutParams.height);
//                return moreFunLayoutParams.getViewType();
//            }
//        }
        if (titleViews!=null&&titleViews.contains(views.get(position))){
            return 1;
        }

        return super.getItemViewType(position);
    }

    public OnViewHolderVisableChangeListener getOnViewHolderVisableChangeListener() {
        return onViewHolderVisableChangeListener;
    }

    public void setOnViewHolderVisableChangeListener(OnViewHolderVisableChangeListener onViewHolderVisableChangeListener) {
        this.onViewHolderVisableChangeListener = onViewHolderVisableChangeListener;
    }

    public void setOnViewVisbilityChangeListener(MoreFunTitleViewGrop.OnViewVisbilityChangeListener onViewVisbilityChangeListener) {
        this.onViewVisbilityChangeListener = onViewVisbilityChangeListener;
    }

    public class MoreFunViewHolder extends RecyclerView.ViewHolder{

        public MoreFunViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void addView(View childView, MoreFunTitleViewGrop.OnViewVisbilityChangeListener onViewVisbilityChangeListener){
            ViewGroup relativeLayout = null;
            if (itemView instanceof MoreFunTitleViewGrop){
                relativeLayout = (MoreFunTitleViewGrop) itemView;
                ((MoreFunTitleViewGrop) relativeLayout).setOnViewVisbilityChangeListener(onViewVisbilityChangeListener);
            }else if (itemView instanceof RelativeLayout)
                relativeLayout = (RelativeLayout) itemView;

            MoreFunListView.MoreFunLayoutParams moreFunLayoutParams = null;
            if (childView.getLayoutParams() instanceof MoreFunListView.MoreFunLayoutParams) {
                moreFunLayoutParams = (MoreFunListView.MoreFunLayoutParams) childView.getLayoutParams();
                if (moreFunLayoutParams != null)
                    Log.d("MoreFunListView", "addView: " + moreFunLayoutParams.height);
            }

            if (relativeLayout!=null) {
                relativeLayout.removeAllViews();
                if (childView.getParent() instanceof ViewGroup){
                    ((ViewGroup) childView.getParent()).removeView(childView);
                }

                relativeLayout.addView(childView,new ViewGroup.LayoutParams(moreFunLayoutParams==null?RelativeLayout.LayoutParams.MATCH_PARENT:moreFunLayoutParams.width
                        ,moreFunLayoutParams==null?RelativeLayout.LayoutParams.WRAP_CONTENT:moreFunLayoutParams.height));
            }
        }
    }

    public interface OnViewHolderVisableChangeListener{
        void onViewAttached(RecyclerView.ViewHolder viewHolder,int position);
        void onViewDetached(RecyclerView.ViewHolder viewHolder,int position);
    }
}
