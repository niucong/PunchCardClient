package com.niucong.punchcardclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.bean.ParcelableMap;
import com.niucong.punchcardclient.net.db.ClassTimeDB;
import com.niucong.punchcardclient.net.db.CourseDB;
import com.niucong.punchcardclient.util.ConstantUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseActivity extends BasicActivity {

    @BindView(R.id.course_name)
    EditText courseName;
    @BindView(R.id.course_teacher)
    EditText courseTeacher;
    @BindView(R.id.course_room)
    EditText courseRoom;
    @BindView(R.id.vatace_button)
    Button vataceButton;
    @BindView(R.id.course_time)
    TextView courseTime;

    private CourseDB courseDB;
    //    private ArrayList<Integer> selectList;
    private Map<Long, ClassTimeDB> selectMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        courseDB = getIntent().getParcelableExtra("courseDB");
        if (courseDB != null) {
            courseName.setText(courseDB.getCourseName());
            courseTeacher.setText(courseDB.getTeacherName());
            courseRoom.setText(courseDB.getRoomName());

            selectMap = new HashMap<>();
            List<ClassTimeDB> timeDBS = LitePal.where("courseId = ?", "" + courseDB.getId()).find(ClassTimeDB.class);
            for (ClassTimeDB timeDB : timeDBS) {
                selectMap.put(timeDB.getId(), timeDB);
            }
            setCourseTime();
        }
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

    @OnClick({R.id.course_time, R.id.vatace_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.course_time:
                if (selectMap == null) {
                    selectMap = new HashMap<>();
                }
                ParcelableMap sMap = new ParcelableMap();
                sMap.setMap(selectMap);
                Intent intent = new Intent(this, SelectClassTimeActivity.class).putExtra("sMap", sMap);
                if (courseDB != null) {
                    intent.putExtra("courseId", courseDB.getId());
                }
                startActivityForResult(intent, 0);
                break;
            case R.id.vatace_button:
                String courseNameStr = courseName.getText().toString();
                String courseTeacherStr = courseTeacher.getText().toString();
                String courseRoomStr = courseRoom.getText().toString();
                if (TextUtils.isEmpty(courseNameStr)) {
                    App.showToast("课程名称不能为空");
                    return;
                }
                if (selectMap == null || selectMap.isEmpty()) {
                    App.showToast("请选择上课时间");
                    return;
                }
                if (courseDB == null) {
                    courseDB = new CourseDB();
                }
                courseDB.setCourseName(courseNameStr);
                courseDB.setTeacherName(courseTeacherStr);
                courseDB.setRoomName(courseRoomStr);

                if (courseDB.getId() == 0) {
                    courseDB.save();
                    App.showToast("保存成功");
                } else {
                    courseDB.update(courseDB.getId());
                    App.showToast("修改成功");
                }
                for (ClassTimeDB timeDB : selectMap.values()) {
                    timeDB.setCourseId(courseDB.getId());
                    timeDB.update(timeDB.getId());
                }
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            ParcelableMap sMap = data.getParcelableExtra("sMap");
            selectMap = sMap.getMap();
            setCourseTime();
        }
    }

    private void setCourseTime() {
        String cTime = "";
        for (String week : ConstantUtil.WEEKS) {
            List<Integer> sectionName = new ArrayList<>();
            for (Map.Entry<Long, ClassTimeDB> dbEntry : selectMap.entrySet()) {
                if (week.equals(dbEntry.getValue().getWeekDay())) {
                    sectionName.add(Integer.valueOf(dbEntry.getValue().getSectionName()));
                }
            }
            if (!sectionName.isEmpty()) {
                cTime += week + "：";
                Collections.sort(sectionName);
                for (int s : sectionName) {
                    cTime += s + "/";
                }
                cTime = cTime.substring(0, cTime.length() - 1);
                cTime += "\n";
            }
        }
        courseTime.setText(cTime);
    }
}
