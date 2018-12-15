package com.example.ourapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryAcctivity";
    private List<imageUriSet> mHisList= new ArrayList<>();           //历史记录
    private Uri tempuri;
    private Toolbar history_show_toolbar;       //toolbar
    private hisAdapter historyAdapter;
    private RecyclerView hisView;

    public static final String CLIP_ICON_DIC = Environment
            .getExternalStorageDirectory()
            + File.separator + "clipIcon";//存在sd卡上的headIcon里面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //*************************************布局设置*************************************
        history_show_toolbar = (Toolbar)findViewById(R.id.toolbar_history);
        setSupportActionBar(history_show_toolbar);

        ActionBar picActBar = getSupportActionBar();
        if(picActBar!=null){
            picActBar.setDisplayHomeAsUpEnabled(true);
            picActBar.setHomeButtonEnabled(true);
            picActBar.setTitle("历史记录");
        }

        //*************************************************************************************

        initHisList();
        hisView = (RecyclerView)findViewById(R.id.hisImage);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        hisView.setLayoutManager(layoutManager);

        historyAdapter = new hisAdapter(mHisList);
        hisView.setAdapter(historyAdapter);

        historyAdapter.setOnItemClickListener(new hisAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                imageUriSet hisChoosed = mHisList.get(position);
                Uri hisUri = hisChoosed.getImageUri();
                Toast.makeText(HistoryActivity.this,"请求成功，转向转换界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HistoryActivity.this,Activity_camera.class);
                intent.putExtra("photoUri",hisUri.toString());
                startActivity(intent);
                finish();
            }
        });

    }

    private void initHisList() {

        mHisList.clear();
        //获取历史记录
        File path = new File(CLIP_ICON_DIC);
        File[] files = path.listFiles();          // 读取文件夹下文件

        if(files!=null){             //判断文件组是否为空
            for(File file : files){
                if(file.isDirectory()){   //判断是否为文件夹
                    Log.i(TAG,"目录下含有文件夹");
                }
                else{
                    String fileName = file.getName();
//                    if(fileName.endsWith(".png")&&file.length()!=0){            //如果是图片
//                        tempuri = PhotoClipperUtil.getUriFromFile(this,file);
                    if(fileName.startsWith("history")&&file.length()!=0){
                        tempuri = PhotoClipperUtil.getImageContentUri(this,file);
                        imageUriSet hisUri = new imageUriSet(tempuri);
                        hisUri.setImagePath(CLIP_ICON_DIC + File.separator + fileName);   //设置图片路径
                        mHisList.add(hisUri);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
