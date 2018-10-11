package com.niucong.punchcardclient;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityMainBinding;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;
import com.niucong.punchcardclient.net.bean.SignBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BasicActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setContentView(R.layout.activity_main);
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.setHandlers(new MainClickHandlers());

        if (App.sp.getInt("type", 0) == 3) {
//            binding.mainMember.setVisibility(View.INVISIBLE);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//        updateTime();
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EventBus.getDefault().register(this);
        updateTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicBean bean) {
        Log.d("SignListActivity", "code=" + bean.getCode());
        if (bean.getCode() == 2) {
            updateTime();
        }
    }

    private void updateTime() {

        String start = App.sp.getString("start", "");
        String end = App.sp.getString("end", "");
        Log.d("MainActivity", "start=" + start + "，end=" + end);
        String msg = "打卡时间：";
        if (start.length() > 10) {
            SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
            if (start.substring(0, 10).equals(YMD.format(new Date()))) {
                msg += start + "——";
                if (end.length() > 10) {
                    if (end.substring(0, 10).equals(YMD.format(new Date()))) {
                        msg += end;
                    } else {
                        msg += "无";
                    }
                }
            } else {
                msg += "无";
            }
        } else {
            msg += "无";
        }
        binding.mainTime.setText(msg);
    }

    public class MainClickHandlers {
        public void onClickName(View v) {
            Log.d("MainActivity", "MainClickHandlers");
            switch (v.getId()) {
                case R.id.main_sign:
                    addSubscription(getApi().sign(), new ApiCallback<SignBean>() {
                        @Override
                        public void onSuccess(SignBean model) {
                            if (model != null) {
                                App.showToast("" + model.getMsg());
                                if (model.getCode() == 1) {
                                    long start = model.getStartTime();
                                    long end = model.getEndTime();
                                    SimpleDateFormat YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    App.sp.putString("start", YMDHMS.format(new Date(start)));
                                    App.sp.putString("end", YMDHMS.format(new Date(end)));
                                    App.sp.commit();
                                    updateTime();
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
                            App.showToast("签到失败");
                        }
                    });
                    break;
                case R.id.main_schedule:
                    startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                    break;
                case R.id.main_course:
                    startActivity(new Intent(MainActivity.this, CourseListActivity.class));
                    break;
                case R.id.main_calendar:
                    startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                    break;
                case R.id.main_project:

                    break;
                case R.id.main_member:
                    startActivity(new Intent(MainActivity.this, MemberListActivity.class));
                    break;
                case R.id.main_attendance:
                    startActivity(new Intent(MainActivity.this, SignListActivity.class));
                    break;
                case R.id.main_vacate:
                    startActivity(new Intent(MainActivity.this, VacateListActivity.class));
                    break;
                case R.id.main_plan:
                    startActivity(new Intent(MainActivity.this, PlanListActivity.class));
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_logout:
                App.sp.putInt("userId", 0);
                App.sp.commit();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
