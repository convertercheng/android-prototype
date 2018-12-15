package com.example.ourapplication;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by 10065 on 2018/11/1.
 */

public class bgAdapter extends RecyclerView.Adapter<bgAdapter.ViewHolder> {

    private Context mContext;
    private List<imageUriSet> mbgList;
    private OnItemClickListener mItemClickListener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView bgImage;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            bgImage = (ImageView)view.findViewById(R.id.bgImage);
        }
    }

    public bgAdapter(List<imageUriSet> bgList){
        mbgList = bgList;
    }

    @Override
    public bgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.background_layout, parent, false);
        bgAdapter.ViewHolder holder = new bgAdapter.ViewHolder(view);
        return holder;
    }


    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    @Override
    public void onBindViewHolder(bgAdapter.ViewHolder holder, final int position) {
        imageUriSet mBg = mbgList.get(position);
        holder.bgImage.setImageURI(mBg.getImageUri());

        holder.itemView.setOnClickListener(new View.OnClickListener(){   //item点击事件
            @Override
            public void onClick(View v) {          //背景点击事件
                if(mItemClickListener!=null){
                    mItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return mbgList.size(); }

}


