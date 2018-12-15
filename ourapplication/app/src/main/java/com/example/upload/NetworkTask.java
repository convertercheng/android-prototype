package com.example.upload;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ourapplication.LoginActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkTask extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return doPost(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if(!"error".equals(result)) {
            Log.i("upload", "图片地址 " + Constant.BASE_URL + result);
//            Glide.with(this)
//                    .load(Constant.BASE_URL + result)
//                    .into(mPictureIv);
        }
    }

    private String doPost(String imagePath) {//这里到时候多加一个传入的用户名值的参数
        OkHttpClient mOkHttpClient = new OkHttpClient();

        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID,把用户名传进去
        String userName = LoginActivity.userName;//这里实际为登陆的用户名值，根据用户名在服务器上创立个人文件夹
        builder.addFormDataPart("userId", userName);
        builder.addFormDataPart("image", imagePath,
                RequestBody.create(MediaType.parse("image/png"), new File(imagePath)));

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(Constant.BASE_URL + "/UploadServlet")
                .post(requestBody)
                .build();

        Log.d("upload", "请求地址 " + Constant.BASE_URL + "/UploadServlet");
        try{
            Response response = mOkHttpClient.newCall(request).execute();
            Log.d("upload", "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d("upload", "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
