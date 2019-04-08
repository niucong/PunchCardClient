package com.niucong.punchcardclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityPlanBinding;
import com.niucong.punchcardclient.dialog.DateSelectDialog;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;
import com.niucong.punchcardclient.net.bean.MemberListBean;
import com.niucong.punchcardclient.net.db.MemberDB;
import com.niucong.punchcardclient.net.db.PlanDB;
import com.niucong.punchcardclient.util.ConstantUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanActivity extends BasicActivity {

    ActivityPlanBinding binding;

    private PlanDB db;

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

        db = getIntent().getParcelableExtra("PlanDB");
        if (db == null) {
            actionBar.setTitle("新建计划");
        } else {

            binding.planName.setText(db.getName());
            binding.planCreator.setVisibility(View.VISIBLE);
            binding.planCreator.setText("创建者：" + db.getCreatorName());
            binding.planCreate.setVisibility(View.VISIBLE);
            binding.planCreate.setText("创建时间：" + ConstantUtil.YMDHM.format(new Date(db.getCreateTime())));

            String names = "";
            Log.d("PlanAdapter", "members=" + db.getMembers());
            try {
                List<MemberDB> members = JSON.parseArray(db.getMembers(), MemberDB.class);
                for (MemberDB member : members) {
                    names += "，" + member.getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("PlanAdapter", "names=" + names);
            if (names.length() > 0) {
                names = names.substring(1);
                binding.planMembers.setText(names);
                binding.planMembersLl.setVisibility(View.VISIBLE);
            } else {
                binding.planMembersLl.setVisibility(View.GONE);
            }

            binding.planStart.setText(ConstantUtil.YMDHM.format(new Date(db.getStartTime())));
            binding.planEnd.setText(ConstantUtil.YMDHM.format(new Date(db.getEndTime())));

            if (db.getEditTime() > 0) {
                binding.planEdit.setVisibility(View.VISIBLE);
                binding.planEdit.setText("修改时间：" + ConstantUtil.YMDHM.format(new Date(db.getEditTime())));
            }

            binding.planStatusLl.setVisibility(View.VISIBLE);
            binding.planButton.setVisibility(View.GONE);
            if (db.getForceFinish() == 0) {
                if (db.getStartTime() > System.currentTimeMillis()) {
                    setTextStutas("未开始", Color.argb(168, 0, 0, 255));
                    if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                        binding.planIsfinish.setVisibility(View.VISIBLE);
                        binding.planIsfinish.setText("取消计划");
                        binding.planIsfinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                binding.planIsfinishCause.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                                binding.planButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                            }
                        });
                    }
                } else if (db.getEndTime() > System.currentTimeMillis()) {
                    setTextStutas("进行中", Color.argb(168, 0, 255, 0));
                    if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                        binding.planIsfinish.setVisibility(View.VISIBLE);
                        binding.planIsfinish.setText("终止计划");
                        binding.planIsfinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                binding.planIsfinishCause.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                                binding.planButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                            }
                        });
                    }
                } else {
                    setTextStutas("已结束", Color.argb(168, 0, 0, 0));
                }
            } else if (db.getForceFinish() == 1) {
                setTextStutas("已取消", Color.argb(168, 255, 0, 0));
            } else {
                setTextStutas("已终止", Color.argb(168, 255, 0, 0));
            }

            binding.planName.setEnabled(false);
            binding.planMembers.setEnabled(false);
            binding.planStart.setEnabled(false);
            binding.planEnd.setEnabled(false);

            binding.planName.setBackgroundColor(Color.alpha(0));
            binding.planName.setTextColor(Color.GRAY);
            binding.planMembers.setTextColor(Color.GRAY);
            binding.planStart.setTextColor(Color.GRAY);
            binding.planEnd.setTextColor(Color.GRAY);

        }
    }

    private void setTextStutas(String msg, int color) {
        binding.planStatus.setText(msg);
        binding.planStatus.setTextColor(color);
    }

    public class PlanClickHandlers {
        public void onClickName(View v) {
            switch (v.getId()) {
                case R.id.plan_members:
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
                        binding.planMembers.setText(names);
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
                final long startDate = ConstantUtil.YMDHM.parse(start).getTime();
                final long endDate = ConstantUtil.YMDHM.parse(end).getTime();
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
            fields.put("forceFinish", "" + (db.getStartTime() > System.currentTimeMillis() ? 1 : 2));
            fields.put("cause", binding.planIsfinishCause.getText().toString().trim());
        }
        addSubscription(getApi().plan(fields), new ApiCallback<BasicBean>() {
            @Override
            public void onSuccess(BasicBean model) {
                if (model != null) {
                    if (model.getCode() == 1) {
                        setResult(RESULT_OK);
                        finish();
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
                App.showToast("操作失败");
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
