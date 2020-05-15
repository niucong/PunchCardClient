package com.niucong.punchcardclient;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bin.david.form.core.SmartTable;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.ScheduleListBean;
import com.niucong.punchcardclient.net.db.ClassTimeDB;
import com.niucong.punchcardclient.net.db.ScheduleDB;
import com.niucong.punchcardclient.table.Schedule;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作息表
 */
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

        if (LitePal.count(ScheduleDB.class) == 0) {
            App.showToast("请先刷新作息表");
        } else {
            refreshScheduleList();
        }
    }

    private void refreshScheduleList() {
        final List<Schedule> schedules = new ArrayList<>();
        for (ScheduleDB scheduleDB : LitePal.findAll(ScheduleDB.class)) {
            schedules.add(new Schedule(scheduleDB.getTimeRank(), scheduleDB.getSectionName(), scheduleDB.getTime()));
        }

        table.setData(schedules);
        table.getConfig().setShowTableTitle(false);
        table.setZoom(true, 2, 0.2f);
    }

    private void getScheduleList() {
        Map<String, String> fields = new HashMap<>();
        Log.d("ScheduleActivity", "fields=" + fields.toString());
        addSubscription(getApi().scheduleList(fields), new ApiCallback<ScheduleListBean>() {
            @Override
            public void onSuccess(ScheduleListBean model) {
                if (model != null) {
                    if (model.getCode() == 1) {
                        if (LitePal.count(ScheduleDB.class) > 0) {
                            LitePal.deleteAll(ScheduleDB.class);
                        }
                        LitePal.saveAll(model.getList());

                        if (LitePal.count(ClassTimeDB.class) > 0) {
                            LitePal.deleteAll(ClassTimeDB.class);
                        }
                        List<ClassTimeDB> timeDBS = new ArrayList<>();
                        for (ScheduleDB scheduleDB : model.getList()) {
                            try {
                                Integer.valueOf(scheduleDB.getSectionName());

                                ClassTimeDB monday = new ClassTimeDB();
                                monday.setSectionName(scheduleDB.getSectionName());
                                monday.setWeekDay("星期一");
                                timeDBS.add(monday);

                                ClassTimeDB tuesday = new ClassTimeDB();
                                tuesday.setSectionName(scheduleDB.getSectionName());
                                tuesday.setWeekDay("星期二");
                                timeDBS.add(tuesday);

                                ClassTimeDB wednesday = new ClassTimeDB();
                                wednesday.setSectionName(scheduleDB.getSectionName());
                                wednesday.setWeekDay("星期三");
                                timeDBS.add(wednesday);

                                ClassTimeDB thursday = new ClassTimeDB();
                                thursday.setSectionName(scheduleDB.getSectionName());
                                thursday.setWeekDay("星期四");
                                timeDBS.add(thursday);

                                ClassTimeDB friday = new ClassTimeDB();
                                friday.setSectionName(scheduleDB.getSectionName());
                                friday.setWeekDay("星期五");
                                timeDBS.add(friday);

                                ClassTimeDB saturday = new ClassTimeDB();
                                saturday.setSectionName(scheduleDB.getSectionName());
                                saturday.setWeekDay("星期六");
                                timeDBS.add(saturday);

                                ClassTimeDB sunday = new ClassTimeDB();
                                sunday.setSectionName(scheduleDB.getSectionName());
                                sunday.setWeekDay("星期日");
                                timeDBS.add(sunday);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        LitePal.saveAll(timeDBS);

                        refreshScheduleList();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_refresh:
                getScheduleList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
