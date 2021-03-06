package com.niucong.punchcardclient.app;

import android.app.Application;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.niucong.punchcardclient.util.SharedPrefUtil;

import org.litepal.LitePal;

public class App extends Application {

    public static App app;

    public static SharedPrefUtil sp;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sp = new SharedPrefUtil(app, "PunchCard");
        LitePal.initialize(this);
        Stetho.initializeWithDefaults(this);

//        //TODO 集成：1.4、初始化数据服务SDK、初始化设备信息并启动推送服务
//// 初始化BmobSDK
//        Bmob.initialize(this, "d82f0138681a35ea0ab3b7194f0e5a22");
//// 使用推送服务时的初始化操作
//        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
//            @Override
//            public void done(BmobInstallation bmobInstallation, BmobException e) {
//                if (e == null) {
//                    Log.i("App", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
//                    sp.putString("bmobID", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
//                    sp.commit();
//                } else {
//                    Log.e("App", e.getMessage());
//                }
//            }
//        });
//// 启动推送服务
//        BmobPush.startWork(this);
    }

    public static void showToast(String text) {
        Toast.makeText(app, text, Toast.LENGTH_LONG).show();
    }
}
