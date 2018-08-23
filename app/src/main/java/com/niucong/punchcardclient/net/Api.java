package com.niucong.punchcardclient.net;

import com.niucong.punchcardclient.net.bean.BasicBean;
import com.niucong.punchcardclient.net.bean.LoginBean;
import com.niucong.punchcardclient.net.bean.MemberListBean;
import com.niucong.punchcardclient.net.bean.PlanListBean;
import com.niucong.punchcardclient.net.bean.SignBean;
import com.niucong.punchcardclient.net.bean.SignListBean;
import com.niucong.punchcardclient.net.bean.VacateListBean;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Observable<LoginBean> login(@FieldMap Map<String, String> fields);

    @POST("memberList")
    Observable<MemberListBean> memberList();

    @POST("sign")
    Observable<SignBean> sign();

    @FormUrlEncoded
    @POST("signList")
    Observable<SignListBean> signList(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("vacate")
    Observable<BasicBean> vacate(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("vacateList")
    Observable<VacateListBean> vacateList(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("plan")
    Observable<BasicBean> plan(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("planList")
    Observable<PlanListBean> planList(@FieldMap Map<String, String> fields);

}
