package com.yutao.ytutils.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yutao.ytutils.R;
import com.yutao.ytutils.ui.adapter.viewholder.EmptyViewHolder;

import java.util.List;

/**
 * a：余涛
 * b：1054868047
 * c：2018/9/27 16:35
 * d：展示空视图的adapter
 */
public abstract class MoreFunAdapter extends RecyclerView.Adapter {
    protected boolean isShowNoDataView = true;//是否展示空视图
    protected int emptyViewType = -1;
    protected String emptyMessage = "";
    protected int emptyIcon = R.mipmap.icon_empty_2;

    protected Context mContext;

    public MoreFunAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == emptyViewType){
            return new EmptyViewHolder(mContext);
        }
        return createNormalViewHolder(viewGroup,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof EmptyViewHolder){
            ((EmptyViewHolder) viewHolder).setMessage(emptyMessage);
            ((EmptyViewHolder) viewHolder).setEmptyImageResource(emptyIcon);
        }else{
            onBindNormalViewHodler(viewHolder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowNoDataView&&(getDataList() == null||getDataList().size()==0)){
            return emptyViewType;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return (getDataList() == null||getDataList().size()==0)?(isShowNoDataView?1:0):getDataList().size();
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
        notifyDataSetChanged();
    }

    public int getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(int emptyIcon) {
        this.emptyIcon = emptyIcon;
        notifyDataSetChanged();
    }

    public boolean isShowNoDataView() {
        return isShowNoDataView;
    }

    public void setShowNoDataView(boolean showNoDataView) {
        isShowNoDataView = showNoDataView;
        notifyDataSetChanged();
    }

    public abstract List getDataList();
    public abstract RecyclerView.ViewHolder createNormalViewHolder( ViewGroup viewGroup, int viewType);
    public abstract void onBindNormalViewHodler( RecyclerView.ViewHolder viewHolder, int position);
}
