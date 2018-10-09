package com.niucong.punchcardclient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.ScheduleListBean;
import com.niucong.punchcardclient.net.db.ScheduleDB;
import com.niucong.punchcardclient.table.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseActivity extends BasicActivity {

    @BindView(R.id.schedule_table)
    SmartTable<Course> table;

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
                        final List<Course> courses = new ArrayList<>();
                        final List<ScheduleDB> list = model.getList();
                        for (ScheduleDB scheduleDB : list) {
                            try {
                                Integer.valueOf(scheduleDB.getSectionName());
                                courses.add(new Course(scheduleDB.getTimeRank(), scheduleDB.getSectionName(),
                                        "", "", "", "", "", "", ""));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        Column<String> timeRank = new Column<>("时段", "timeRank");
                        timeRank.setAutoMerge(true);
                        Column<String> sectionName = new Column<>("节次", "sectionName");
                        sectionName.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                            @Override
                            public void onClick(Column<String> column, String value, String s, int position) {
                                for (ScheduleDB scheduleDB : list) {
                                    if (scheduleDB.getSectionName().equals(value)) {
                                        App.showToast("第" + value + "节课 " + scheduleDB.getTime());
                                        break;
                                    }
                                }
                            }
                        });
                        Column<String> monday = new Column<>("一", "monday");
                        setColumn(monday);
                        Column<String> tuesday = new Column<>("二", "tuesday");
                        setColumn(tuesday);
                        Column<String> wednesday = new Column<>("三", "wednesday");
                        setColumn(wednesday);
                        Column<String> thursday = new Column<>("四", "thursday");
                        setColumn(thursday);
                        Column<String> friday = new Column<>("五", "friday");
                        setColumn(friday);
                        Column<String> saturday = new Column<>("六", "saturday");
                        setColumn(saturday);
                        Column<String> sunday = new Column<>("日", "sunday");
                        setColumn(sunday);

                        TableData<Course> tableData = new TableData<>("校历", courses, timeRank, sectionName,
                                monday, tuesday, wednesday, thursday, friday, saturday, sunday);
                        table.setTableData(tableData);
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

    private void setColumn(Column<String> column) {
        column.setAutoMerge(true);
        column.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                App.showToast(value);
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
