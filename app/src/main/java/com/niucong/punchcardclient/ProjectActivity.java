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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityProjectBinding;
import com.niucong.punchcardclient.dialog.DateSelectDialog;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;
import com.niucong.punchcardclient.net.bean.MemberListBean;
import com.niucong.punchcardclient.net.db.MemberDB;
import com.niucong.punchcardclient.net.db.ProjectDB;
import com.niucong.punchcardclient.util.ConstantUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends BasicActivity {

    ActivityProjectBinding binding;

    private ProjectDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_project);
        binding.setHandlers(new ProjectClickHandlers());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = getIntent().getParcelableExtra("ProjectDB");
        if (db == null) {
            actionBar.setTitle("新建项目");
        } else {
            binding.projectName.setText(db.getName());
            binding.projectCreator.setVisibility(View.VISIBLE);
            binding.projectCreator.setText("创建者：" + db.getCreatorName());
            binding.projectCreate.setVisibility(View.VISIBLE);
            binding.projectCreate.setText("创建时间：" + ConstantUtil.YMDHM.format(new Date(db.getCreateTime())));

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
                binding.projectMembers.setText("关联人员：" + names);
                binding.projectMembersLl.setVisibility(View.VISIBLE);
            } else {
                binding.projectMembersLl.setVisibility(View.GONE);
            }

            binding.projectStart.setText(ConstantUtil.YMDHM.format(new Date(db.getStartTime())));
            binding.projectEnd.setText(ConstantUtil.YMDHM.format(new Date(db.getEndTime())));

            if (db.getCloseTime() > 0) {
                binding.projectEdit.setVisibility(View.VISIBLE);
                binding.projectEdit.setText("关闭时间：" + ConstantUtil.YMDHM.format(new Date(db.getCloseTime())));
            }

            binding.projectStatusLl.setVisibility(View.VISIBLE);
            binding.projectButton.setVisibility(View.GONE);
            if (db.getForceFinish() == 0) {
                if (db.getStartTime() > System.currentTimeMillis()) {
                    setTextStutas("未开始", Color.argb(168, 0, 0, 255));
                    if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                        binding.projectIsfinish.setVisibility(View.VISIBLE);
                        binding.projectIsfinish.setText("取消项目");
                        binding.projectIsfinishCause.setVisibility(View.VISIBLE);
                        binding.projectButton.setVisibility(View.VISIBLE);
                    }
                } else if (db.getEndTime() > System.currentTimeMillis()) {
                    setTextStutas("进行中", Color.argb(168, 0, 255, 0));
                    if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                        binding.projectIsfinish.setVisibility(View.VISIBLE);
                        binding.projectIsfinish.setText("终止项目");
                        binding.projectIsfinishCause.setVisibility(View.VISIBLE);
                        binding.projectButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    setTextStutas("已结束", Color.argb(168, 0, 0, 0));
                }
            } else if (db.getForceFinish() == 1) {
                setTextStutas("已取消", Color.argb(168, 255, 0, 0));
            } else {
                setTextStutas("已终止", Color.argb(168, 255, 0, 0));
            }

            binding.projectName.setEnabled(false);
            binding.projectMembers.setEnabled(false);
            binding.projectStart.setEnabled(false);
            binding.projectEnd.setEnabled(false);

            binding.projectName.setBackgroundColor(Color.alpha(0));
            binding.projectName.setTextColor(Color.GRAY);
            binding.projectMembers.setTextColor(Color.GRAY);
            binding.projectStart.setTextColor(Color.GRAY);
            binding.projectEnd.setTextColor(Color.GRAY);

        }
    }

    private void setTextStutas(String msg, int color) {
        binding.projectStatus.setText(msg);
        binding.projectStatus.setTextColor(color);
    }

    public class ProjectClickHandlers {
        public void onClickName(View v) {
            switch (v.getId()) {
                case R.id.project_members:
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
                case R.id.project_start:
                    DateSelectDialog.selectDateTime(ProjectActivity.this, binding.projectStart);
                    break;
                case R.id.project_end:
                    DateSelectDialog.selectDateTime(ProjectActivity.this, binding.projectEnd);
                    break;
                case R.id.project_button:
                    submitProject();
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
                        Log.i("ProjectActivity", which + ";" + isChecked);
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
                        binding.projectMembers.setText(names);
                    }
                })
                .show();
    }

    private void submitProject() {
        Map<String, String> fields = new HashMap<>();
        if (db == null) {
            String name = binding.projectName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                App.showToast("项目名称不能为空");
                return;
            }
            String start = binding.projectStart.getText().toString().trim();
            String end = binding.projectEnd.getText().toString().trim();
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
                fields.put("remark", binding.projectRemark.getText().toString());

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
            fields.put("cause", binding.projectIsfinishCause.getText().toString().trim());
        }
        addSubscription(getApi().project(fields), new ApiCallback<BasicBean>() {
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
