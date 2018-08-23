package com.niucong.punchcardclient;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityMainBinding;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;

public class MainActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setContentView(R.layout.activity_main);
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.setHandlers(new MainClickHandlers());

        if (App.sp.getInt("type", 0) == 3) {
//            binding.mainMember.setVisibility(View.INVISIBLE);
        }
    }

    public class MainClickHandlers {
        public void onClickName(View v) {
            Log.d("MainActivity", "MainClickHandlers");
            switch (v.getId()) {
                case R.id.main_sign:
                    addSubscription(getApi().sign(), new ApiCallback<BasicBean>() {
                        @Override
                        public void onSuccess(BasicBean model) {
                            if (model != null) {
                                App.showToast("" + model.getMsg());
                                if (model.getCode() == 1) {

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
                case R.id.main_sync:

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

}
