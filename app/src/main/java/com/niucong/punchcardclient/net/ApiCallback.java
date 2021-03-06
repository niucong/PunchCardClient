package com.niucong.punchcardclient.net;


import java.net.SocketTimeoutException;

import io.reactivex.observers.ResourceObserver;
import retrofit2.HttpException;

/**
 *
 */
abstract public class ApiCallback<M> extends ResourceObserver<M> {

    public abstract void onSuccess(M model);

    public abstract void onFailure(String msg);

    public abstract void onFinish();


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //httpException.response().errorBody().string()
            int code = httpException.code();
            String msg = httpException.getMessage();
            if (code == 504) {
                msg = "网络不给力";
            }
            if (code == 502 || code == 404) {
                msg = "服务器异常";
            }
            onFailure(msg);
        } else if (e instanceof SocketTimeoutException) {
            onFailure("网络超时");
        } else {
            onFailure(e.getMessage());
        }
        onFinish();
    }

    @Override
    public void onNext(M model) {
        onFinish();
        onSuccess(model);
    }

    @Override
    public void onComplete() {
        onFinish();
    }
}
