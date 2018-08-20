package com.niucong.punchcardclient.net;

import com.niucong.punchcardclient.net.bean.LoginBean;
import com.niucong.punchcardclient.net.bean.SignInBean;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Observable<LoginBean> login(@FieldMap Map<String, String> fields);

    @POST("signIn")
    Observable<SignInBean> signIn();

}
