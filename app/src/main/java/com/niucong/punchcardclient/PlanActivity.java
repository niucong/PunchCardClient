package com.niucong.punchcardclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityPlanBinding;
import com.niucong.punchcardclient.dialog.DateSelectDialog;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.MemberListBean;
import com.niucong.punchcardclient.net.bean.VacateBean;
import com.niucong.punchcardclient.net.db.CoursePlanDB;
import com.niucong.punchcardclient.net.db.MemberDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanActivity extends BasicActivity {

    ActivityPlanBinding binding;

    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private CoursePlanDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plan);
        binding.setHandlers(new PlanClickHandlers());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public class PlanClickHandlers {
        public void onClickName(View v) {
            switch (v.getId()) {
                case R.id.plan_owners:
                    if (list != null) {
                        selectMember();
                    } else {
                        addSubscription(getApi().memberList(), new ApiCallback<MemberListBean>() {
                            @Override
                            public void onSuccess(MemberListBean model) {
                                if (model != null) {
                                    if (model.getCode() == 1) {
                                        list = model.getList();
                                        int size = list.size();
                                        items = new String[size];
                                        booleans = new boolean[size];
                                        for (int i = 0; i < size; i++) {
                                            items[i] = list.get(i).getName();
                                            booleans[i] = false;
                                        }
                                        selectMember();
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
                    break;
                case R.id.plan_start:
                    DateSelectDialog.selectDateTime(PlanActivity.this, binding.planStart);
                    break;
                case R.id.plan_end:
                    DateSelectDialog.selectDateTime(PlanActivity.this, binding.planEnd);
                    break;
                case R.id.plan_button:
                    submitPlan();
                    break;
            }
        }
    }

    List<MemberDB> list;
    String[] items = null;
    boolean[] booleans = null;

    private void selectMember() {
        App.showToast("选择成员");
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("多选列表")
                .setMultiChoiceItems(items, booleans, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Log.i("PlanActivity", which + ";" + isChecked);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String names = "";
                        for (int i = 0; i < booleans.length; i++) {
                            if (booleans[i]) {
                                names += "；" + items[i];
                            }
                        }
                        if (names.length() > 0) {
                            names = names.substring(1);
                        }
                        binding.planOwners.setText(names);
                    }
                })
                .show();
    }

    private void submitPlan() {
        Map<String, String> fields = new HashMap<>();
        if (db == null) {
            String name = binding.planName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                App.showToast("计划名称不能为空");
                return;
            }
            String start = binding.planStart.getText().toString().trim();
            String end = binding.planEnd.getText().toString().trim();
            if (TextUtils.isEmpty(start)) {
                App.showToast("开始时间不能为空");
                return;
            } else if (TextUtils.isEmpty(end)) {
                App.showToast("结束时间不能为空");
                return;
            }
            try {
                final long startDate = YMDHM.parse(start).getTime();
                final long endDate = YMDHM.parse(end).getTime();
                if (startDate != 0 && endDate != 0) {
                    if (startDate > endDate) {
                        App.showToast("开始时间不能晚于结束时间");
                        return;
                    } else if (startDate == endDate) {
                        App.showToast("开始时间不能等于结束时间");
                        return;
                    }
                }
                fields.put("name", name);
                fields.put("start", "" + startDate);
                fields.put("end", "" + endDate);

                if (booleans != null) {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < booleans.length; i++) {
                        if (booleans[i]) {
                            JSONObject object = new JSONObject();
                            object.put("id", list.get(i).getId());
                            object.put("name", list.get(i).getName());
                            array.add(object);
                        }
                    }
                    fields.put("members", array.toJSONString());
                }
            } catch (ParseException e) {
                e.printStackTrace();
                App.showToast("时间有误");
                return;
            }
        } else {
            fields.put("serverId", "" + db.getId());
            fields.put("forceFinish", "" + 1);
            fields.put("cause", binding.planName.getText().toString().trim());
        }
        addSubscription(getApi().plan(fields), new ApiCallback<VacateBean>() {
            @Override
            public void onSuccess(VacateBean model) {
                if (model != null) {
                    App.showToast("" + model.getMsg());
                    if (model.getCode() == 1) {
                        setResult(RESULT_OK);
                        finish();
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
                App.showToast("请假/批假失败");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
