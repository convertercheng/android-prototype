package com.example.ourapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.util.LruCache;
import java.util.List;

public class hisAdapter extends RecyclerView.Adapter<hisAdapter.ViewHolder> {

    private Context mContext;
    private List<imageUriSet> mHisSet;
    private OnItemClickListener mItemClickListener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView hisPic;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            hisPic = (ImageView)view.findViewById(R.id.hisImage);
        }
    }

    public hisAdapter(List<imageUriSet> hisSet){
        mHisSet = hisSet;
    }


    public interface OnItemClickListener {         //历史记录点击
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {        //历史纪录点击
        this.mItemClickListener = listener;
    }


    @Override
    public hisAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_layout, parent, false);
        hisAdapter.ViewHolder holder = new hisAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(hisAdapter.ViewHolder holder, final int position) {
        imageUriSet mHis = mHisSet.get(position);
//        Bitmap bitmap;
//        bitmap = BitmapFactory.decodeFile(mHis.getImagePath());
        holder.hisPic.setImageURI(mHis.getImageUri());

        holder.itemView.setOnClickListener(new View.OnClickListener(){   //item点击事件
            @Override
            public void onClick(View v) {          //历史记录点击事件
                if(mItemClickListener!=null){
                    mItemClickListener.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHisSet.size();
    }
}
