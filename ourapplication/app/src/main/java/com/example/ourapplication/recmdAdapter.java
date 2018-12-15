package com.example.ourapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.LruCache;
import android.util.Log;

import java.util.HashSet;
import java.util.List;

/**
 * Created by 10065 on 2018/10/29.
 */

public class recmdAdapter extends RecyclerView.Adapter<recmdAdapter.ViewHolder> {

    private Context mContext;
    private List<imageUriSet> mRecmdSet;
    private RecyclerView mRecyclerView;

    private LruCache<String, Bitmap> lruCache;  //图片缓存技术的核心类
    private HashSet<BitmapWorkerTask> taskCollection;  //存储当前正在执行的任务集合
    private boolean isFirstEnter = true;    //页面首次进入的标记
    private int mFirstVisibleItemPosition;   //前RecyclerView 第一个可见条目的索引
    private int mLastVisibleItemPosition;   //当前RecyclerView 最后一个可见条目的索引
    String Tag;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView recmdView;
        TextView recmdParam;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            recmdView = (ImageView)view.findViewById(R.id.recmdImage);
            recmdParam = (TextView)view.findViewById(R.id.recmdParam);
        }
    }

    public recmdAdapter(Context context, RecyclerView recView, List<imageUriSet> recmdSet){
        this.mContext = context;
        this.mRecmdSet = recmdSet;
        this.mRecyclerView = recView;

        Tag = this.getClass().getName();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        mRecyclerView.addOnScrollListener(getOnScrollListener());
        taskCollection = new HashSet<>();
    }

    @NonNull
    private RecyclerView.OnScrollListener getOnScrollListener(){
        return new RecyclerView.OnScrollListener(){
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//只有在页面处于静止时采取加载图片
                    mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    mLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    loadBitmaps();
                }else{//其他状态下 取消当前开启的异步任务
                    cancleAllTasks();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFirstEnter) {
                    //第一次加载时 并不会触发onScrollStateChanged 所以需要手动加载网络图片
                    mFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    mLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    loadBitmaps();
                    isFirstEnter = false;
                }
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.recommand_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        imageUriSet mRec = mRecmdSet.get(position);
        holder.recmdParam.setText(mRec.getImageParam());
        setImageView(holder.recmdView, mRec.getImageUri());
    }

    @Override
    public int getItemCount() {
        return mRecmdSet.size();
    }

    //**************************************************************************
    public Bitmap getBitmapFromMemoryCache(String uri) {             //从缓存中找图片
        return lruCache.get(uri);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {       //图片加入缓存
        if (getBitmapFromMemoryCache(key) == null) {
            lruCache.put(key, bitmap);
        }
    }

    public void setImageView(ImageView image, Uri uri){
        Log.i(Tag,uri+"");
        Bitmap bitmap = getBitmapFromMemoryCache(uri.toString());
        if (bitmap != null) {           //拥有缓存
            image.setImageBitmap(bitmap);
        } else {
            image.setImageURI(uri);
        }

    }

    public void loadBitmaps() {
        for (int i = mFirstVisibleItemPosition; i < mLastVisibleItemPosition + 1; i++) {
            try{
                String imageuri = mRecmdSet.get(i).getImageUri().toString();
                Bitmap cache = getBitmapFromMemoryCache(imageuri);
                if(cache == null){        //没有缓存
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageuri);
                }
                else{
                    ImageView imageview = (ImageView)mRecyclerView.findViewWithTag(imageuri);
                    if (imageview != null && cache != null) {
                        imageview.setImageBitmap(cache);

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void cancleAllTasks() {
        if (taskCollection != null && taskCollection.size() > 0) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
            taskCollection.clear();
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Integer, Bitmap> {
        private String imageUri;
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUri = params[0];
            Uri temp = Uri.parse(params[0]);
            Bitmap bitmap = BitmapFactory.decodeFile(temp.getEncodedPath());
            if (bitmap != null) {
                // 图片加载完成后缓存到LrcCache中
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            ImageView imageView = (ImageView) mRecyclerView.findViewWithTag(imageUri);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }
    }

}
