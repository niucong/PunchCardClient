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
import com.niucong.punchcardclient.util.ConstantUtil;

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
        final List<ScheduleDB> list = LitePal.findAll(ScheduleDB.class);
        if (list.isEmpty()) {
            App.showToast("请先刷新作息表");
            finish();
        }
        final List<Course> courses = new ArrayList<>();
        for (ScheduleDB scheduleDB : list) {
            try {
                String sectionName = scheduleDB.getSectionName();
                Integer.valueOf(sectionName);
                courses.add(new Course(scheduleDB.getTimeRank(), sectionName,
                        getCourseStr(ConstantUtil.WEEKS[0], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[1], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[2], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[3], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[4], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[5], sectionName),
                        getCourseStr(ConstantUtil.WEEKS[6], sectionName)));
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

        TableData<Course> tableData = new TableData<>("课程表", courses, timeRank, sectionName,
                createWeekColumn(ConstantUtil.WEEKS[0], "monday"),
                createWeekColumn(ConstantUtil.WEEKS[1], "tuesday"),
                createWeekColumn(ConstantUtil.WEEKS[2], "wednesday"),
                createWeekColumn(ConstantUtil.WEEKS[3], "thursday"),
                createWeekColumn(ConstantUtil.WEEKS[4], "friday"),
                createWeekColumn(ConstantUtil.WEEKS[5], "saturday"),
                createWeekColumn(ConstantUtil.WEEKS[6], "sunday"));
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
//                App.showToast(value);
                startActivityForResult(new Intent(CourseListActivity.this, CourseActivity.class)
                        .putExtra("courseDB", queryCourseDB(column.getColumnName(), (position + 1) + "")), 0);
            }
        });
        return column;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            createCourses();
        }
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
                monday += "\n@" + courseDB.getRoomName();
            }
            if (!TextUtils.isEmpty(courseDB.getTeacherName())) {
                monday += "\n@" + courseDB.getTeacherName();
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
