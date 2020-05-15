package com.niucong.punchcardclient.net;


import android.util.Log;

import com.niucong.punchcardclient.app.App;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class ApiClient {


    // 定义一个私有构造方法
    private ApiClient() {

    }

    //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
    public static volatile ApiClient instance;

    //定义一个共有的静态方法，返回该类型实例
    public static ApiClient getIstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (ApiClient.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }

    public Retrofit mRetrofit;

    /**
     * 一个 BaseUrl 对应一个此方法 ,
     * 在 BasePresent 中的 attachView 方法中初始化
     * 在 BaseActivity 中添加一个初始化 Apixx.class 的方法
     *
     * @return
     */
    public Retrofit retrofit(String baseUrl) {
//        if (mRetrofit == null) {
        OkHttpClient okHttpClient = getOKHttpClient(null);
        Log.e("ApiClient", "baseUrl=" + baseUrl);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
//        }
        return mRetrofit;
    }

    private OkHttpClient getOKHttpClient(Map<String, String> params) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);//设置超时时间 单位是秒
        builder.readTimeout(60, TimeUnit.SECONDS);//设置读取超时时间 单位是秒
        builder.writeTimeout(60, TimeUnit.SECONDS);//设置写入超时时间 单位是秒
//        //设置禁止Fillder,charls代理抓包
//        if (!BuildConfig.isDebug){
//            builder.proxy(Proxy.NO_PROXY);
//        }

        // Log信息拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("NetWork", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(new NetworkInterceptor(params));
        builder.addInterceptor(loggingInterceptor);

        OkHttpClient mOkHttpClient = builder.build();
        return mOkHttpClient;
    }

    public static class NetworkInterceptor implements Interceptor {

        Map<String, String> params;

        public NetworkInterceptor(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request requestOrigin = chain.request();
            Headers headersOrigin = requestOrigin.headers();
            Headers.Builder builder = headersOrigin.newBuilder();
            builder.set("userId", "" + App.sp.getInt("userId", 0));
            Log.d("ApiClient", "userId=" + App.sp.getInt("userId", 0));
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.set(entry.getKey(), entry.getValue());
                }
            }
            Headers headers = builder.build();
            Request request = requestOrigin.newBuilder().headers(headers).build();
            Response response = chain.proceed(request);
            return response;
        }
    }

}
