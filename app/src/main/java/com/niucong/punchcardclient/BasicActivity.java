package com.niucong.punchcardclient;

import android.os.Bundle;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.Api;
import com.niucong.punchcardclient.net.ApiClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class BasicActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public Api getApi() {
        return ApiClient.getIstance().retrofit(App.sp.getString("url", "")).create(Api.class);
    }

    public void addSubscription(Observable observable, ResourceObserver subscriber) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        mCompositeDisposable.add(subscriber);
//        mCompositeSubscription.add(observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber));
    }
}
