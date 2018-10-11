package com.niucong.punchcardclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.db.ClassTimeDB;
import com.niucong.punchcardclient.net.db.CourseDB;
import com.niucong.punchcardclient.net.db.ScheduleDB;
import com.niucong.punchcardclient.table.Course;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseListActivity extends BasicActivity {

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
        createCourses();
    }

    /**
     * 创建课程表
     */
    private void createCourses() {
        final List<Course> courses = new ArrayList<>();
        final List<ScheduleDB> list = LitePal.findAll(ScheduleDB.class);
        for (ScheduleDB scheduleDB : list) {
            try {
                String sectionName = scheduleDB.getSectionName();
                Integer.valueOf(sectionName);
                courses.add(new Course(scheduleDB.getTimeRank(), sectionName,
                        getCourseStr("星期一", sectionName),
                        getCourseStr("星期二", sectionName),
                        getCourseStr("星期三", sectionName),
                        getCourseStr("星期四", sectionName),
                        getCourseStr("星期五", sectionName),
                        getCourseStr("星期六", sectionName),
                        getCourseStr("星期日", sectionName)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Column<String> timeRank = new Column<>("时段", "timeRank");
        timeRank.setAutoMerge(true);
        Column<String> sectionName = new Column<>("节次", "sectionName");
        sectionName.setFixed(true);
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

        TableData<Course> tableData = new TableData<>("校历", courses, timeRank, sectionName,
                createWeekColumn("星期一", "monday"),
                createWeekColumn("星期二", "tuesday"),
                createWeekColumn("星期三", "wednesday"),
                createWeekColumn("星期四", "thursday"),
                createWeekColumn("星期五", "friday"),
                createWeekColumn("星期六", "saturday"),
                createWeekColumn("星期日", "sunday"));
        table.setTableData(tableData);
        table.getConfig().setShowTableTitle(false);
        table.setZoom(true, 2, 0.2f);
    }

    /**
     * 创建星期x的课程表
     *
     * @param columnName
     * @param fieldName
     * @return
     */
    private Column<String> createWeekColumn(String columnName, String fieldName) {
        Column<String> column = new Column<>(columnName, fieldName);
        column.setAutoMerge(true);
        column.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                Log.d("CourseActivity", "column=" + column.getColumnName());
                Log.d("CourseActivity", "position=" + (position + 1));
                App.showToast(value);
                startActivity(new Intent(CourseListActivity.this, CourseActivity.class)
                        .putExtra("courseDB", queryCourseDB(column.getColumnName(), (position + 1) + "")));
            }
        });
        return column;
    }

    /**
     * 获取课程显示字符串
     *
     * @param weekDay
     * @param sectionName
     * @return
     */
    private String getCourseStr(String weekDay, String sectionName) {
        String monday = "";
        CourseDB courseDB = queryCourseDB(weekDay, sectionName);
        if (courseDB != null) {
            monday += courseDB.getCourseName();
            if (!TextUtils.isEmpty(courseDB.getRoomName())) {
                monday += "@" + courseDB.getRoomName();
            }
            if (!TextUtils.isEmpty(courseDB.getTeacherName())) {
                monday += "@" + courseDB.getTeacherName();
            }
        }
        return monday;
    }

    /**
     * 查询课程
     *
     * @param weekDay
     * @param sectionName
     * @return
     */
    private CourseDB queryCourseDB(String weekDay, String sectionName) {
        ClassTimeDB timeDB = LitePal.where("weekDay = ? and sectionName = ?",
                weekDay, sectionName).findFirst(ClassTimeDB.class);
        if (timeDB == null) {
            return null;
        }
        return LitePal.find(CourseDB.class, timeDB.getCourseId());
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
