package com.exchange_rate;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 林 on 2017/1/9.
 */

public class ExchangeRateApp extends Application {
    OkHttpClient mOkHttpClient;
    LinkedTreeMap<String,List< Handler>>responseTaget;

    @Override
    public void onCreate() {
        super.onCreate();
        responseTaget=new LinkedTreeMap<>();

        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(7676, TimeUnit.MILLISECONDS).
                connectTimeout(7676,TimeUnit.MILLISECONDS)
                .cookieJar(new CookieJar() {
                    List<Cookie> cookies=new ArrayList();//[ZHS_SID=E0343CF8597AC9DBB18942BCC6B921CA; path=/; httponly, extKey1=""; path=/; httponly]
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Log.e("Z","cookies:"+cookies);
                        this.cookies=cookies;
                    }
                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return cookies;
                    }
                })
                .build();
    }
    public void NetRequest(final String Url, final int what, final Handler handler, final String category) {
        try {
            List<Handler>taget= responseTaget.get(Url);
            if(taget!=null){             //有重复的请求
                if(handler!=null)taget.add(handler);
            }else{                       //没有重复的请求
                taget=new ArrayList<>();
                responseTaget.put(Url,taget);
                if(handler!=null)taget.add(handler);
                Request request = new Request.Builder()
                        .url(Url)
                        //  .post(RequestBody.create(MediaType.parse("text/plain"), "发送内容"))//post请求
                        .build();
                Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {//异步
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ResponseTaget(null);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        switch (category){
                            case "String":
                                ResponseTaget(response.body().string());//字符串
                                break;
                            case "ByteArray":
                                ResponseTaget(response.body().bytes());//byte数组
                                break;
                            case "InputStream":
                                ResponseTaget(response.body().byteStream());//输入流   InputStream
                                break;
                        }
                    }//异步
                    public void ResponseTaget(Object str){
                        for(Handler callback: responseTaget.get(Url)){
                            Message msg = callback.obtainMessage();
                            msg.obj=str;
                            msg.what=what;
                            callback.sendMessage(msg);
                        }
                        responseTaget.remove(Url);//请求结束删除记录
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
