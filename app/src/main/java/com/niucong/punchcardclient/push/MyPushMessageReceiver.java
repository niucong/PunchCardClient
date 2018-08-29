package com.niucong.punchcardclient.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.bean.BasicBean;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("MyPushMessageReceiver", "客户端收到推送内容：" + intent.getStringExtra("msg"));
            BasicBean bean = JSON.parseObject(intent.getStringExtra("msg"), BasicBean.class);
            App.showToast(bean.getMsg());
            EventBus.getDefault().post(bean);
        }
    }

}
