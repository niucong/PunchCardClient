package com.niucong.punchcardclient.app;

import android.app.Application;
import android.widget.Toast;

import com.niucong.punchcardclient.util.SharedPrefUtil;

public class App extends Application {

    public static App app;

    public static SharedPrefUtil sp;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sp = new SharedPrefUtil(app, "PunchCard");
    }

    public static void showToast(String text) {
        Toast.makeText(app, text, Toast.LENGTH_LONG).show();
    }
}
