package com.niucong.punchcardclient.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.niucong.punchcardclient.MainActivity;
import com.niucong.punchcardclient.PlanListActivity;
import com.niucong.punchcardclient.ProjectListActivity;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.SignListActivity;
import com.niucong.punchcardclient.VacateListActivity;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.bean.BasicBean;

import org.greenrobot.eventbus.EventBus;

import androidx.core.app.NotificationCompat;
import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("MyPushMessageReceiver", "客户端收到推送内容：" + intent.getStringExtra("msg"));
            BasicBean bean = JSON.parseObject(intent.getStringExtra("msg"), BasicBean.class);
            App.showToast(bean.getMsg());
            EventBus.getDefault().post(bean);
            sendNotificationWithAction(context, bean);
        }
    }

    /**
     * 发送一个点击跳转的消息
     */
    private void sendNotificationWithAction(Context context, BasicBean bean) {
        //获取PendingIntent
        Intent mainIntent;
        if (bean.getCode() == 0) {
            mainIntent = new Intent(context, VacateListActivity.class);
        } else if (bean.getCode() == 1) {
            mainIntent = new Intent(context, PlanListActivity.class);
        } else if (bean.getCode() == 2) {
            mainIntent = new Intent(context, SignListActivity.class);
        } else if (bean.getCode() == 3) {
            mainIntent = new Intent(context, ProjectListActivity.class);
        } else {
            mainIntent = new Intent(context, MainActivity.class);
        }
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //创建 Notification.Builder 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                //点击通知后自动清除
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(bean.getMsg())
                .setContentIntent(mainPendingIntent);

        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //发送通知
        mNotifyManager.notify(3, builder.build());
    }

}
