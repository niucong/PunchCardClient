package com.niucong.punchcardclient;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bin.david.form.core.SmartTable;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.CalendarListBean;
import com.niucong.punchcardclient.net.db.CalendarDB;
import com.niucong.punchcardclient.table.Calendar;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 校历
 */
public class CalendarActivity extends BasicActivity {

    @BindView(R.id.schedule_table)
    SmartTable<Calendar> table;

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

        if (LitePal.count(CalendarDB.class) == 0) {
            App.showToast("请先刷新校历");
        } else {
            refreshCalendarList();
        }
    }

    private void refreshCalendarList() {
        final List<Calendar> calendars = new ArrayList<>();
        for (CalendarDB calendarDB : LitePal.findAll(CalendarDB.class)) {
            calendars.add(new Calendar(calendarDB.getSession(), calendarDB.getWeekly(), calendarDB.getMonth(),
                    calendarDB.getMonday(), calendarDB.getTuesday(), calendarDB.getWednesday(), calendarDB.getThursday(),
                    calendarDB.getFriday(), calendarDB.getSaturday(), calendarDB.getSunday()));
        }

        table.setData(calendars);
        table.getConfig().setShowTableTitle(false);
        table.setZoom(true, 2, 0.2f);
    }

    private void getCalendarList() {
        Map<String, String> fields = new HashMap<>();
        Log.d("CalendarActivity", "fields=" + fields.toString());
        addSubscription(getApi().calendarList(fields), new ApiCallback<CalendarListBean>() {
            @Override
            public void onSuccess(CalendarListBean model) {
                if (model != null) {
                    if (model.getCode() == 1) {
                        if (LitePal.count(CalendarDB.class) > 0) {
                            LitePal.deleteAll(CalendarDB.class);
                        }

                        LitePal.saveAll(model.getList());
                        refreshCalendarList();
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
                getCalendarList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
