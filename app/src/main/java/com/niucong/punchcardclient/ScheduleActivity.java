package com.niucong.punchcardclient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bin.david.form.core.SmartTable;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.ScheduleListBean;
import com.niucong.punchcardclient.net.db.ScheduleDB;
import com.niucong.punchcardclient.table.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleActivity extends BasicActivity {

    @BindView(R.id.schedule_table)
    SmartTable<Schedule> table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WindowManager wm = this.getWindowManager();
        int screenWith = wm.getDefaultDisplay().getWidth();
        table.getConfig().setMinTableWidth(screenWith); //设置最小宽度 屏幕宽度
        getScheduleList();
    }

    private void getScheduleList() {
        Map<String, String> fields = new HashMap<>();
        Log.d("ScheduleActivity", "fields=" + fields.toString());
        addSubscription(getApi().scheduleList(fields), new ApiCallback<ScheduleListBean>() {
            @Override
            public void onSuccess(ScheduleListBean model) {
                if (model != null) {
                    if (model.getCode() == 1) {
                        final List<Schedule> schedules = new ArrayList<>();
                        for (ScheduleDB scheduleDB : model.getList()) {
                            schedules.add(new Schedule(scheduleDB.getTimeRank(), scheduleDB.getSectionName(), scheduleDB.getTime()));
                        }

                        table.setData(schedules);
                        table.getConfig().setShowTableTitle(false);
                        table.setZoom(true, 2, 0.2f);
                    } else {
                        App.showToast("" + model.getMsg());
                    }
                } else {
                    App.showToast("接口错误" + (model == null));
                }
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(String msg) {
                App.showToast("请求失败");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
