package com.yutao.ytutils.ui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;


public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public TextView emptyMessageTV;
    public ImageView emptyIconIV;

    public EmptyViewHolder(Context mContext) {
        super(LayoutInflater.from(mContext).inflate(R.layout.view_empty_view_1, null));
        emptyMessageTV = itemView.findViewById(R.id.emptyMessageTV);
        emptyIconIV = itemView.findViewById(R.id.emptyIconIV);
    }

    public void setMessage(String message){
        emptyMessageTV.setText(message);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) PhoneUtils.dpTopx(itemView.getContext(),300)));
    }

    public void setEmptyImageResource(int rid){
        emptyIconIV.setImageResource(rid);
    }
}
