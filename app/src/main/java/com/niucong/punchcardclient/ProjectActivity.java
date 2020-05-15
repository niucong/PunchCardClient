package com.niucong.punchcardclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

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

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

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
            binding.projectName.setEnabled(false);
            binding.projectMembers.setEnabled(false);
            binding.projectStart.setEnabled(false);
            binding.projectEnd.setEnabled(false);

            binding.projectName.setBackgroundColor(Color.alpha(0));
            binding.projectName.setTextColor(Color.GRAY);
            binding.projectMembers.setTextColor(Color.GRAY);
            binding.projectStart.setTextColor(Color.GRAY);
            binding.projectEnd.setTextColor(Color.GRAY);

            binding.projectName.setText(db.getName());
            binding.projectCreator.setVisibility(View.VISIBLE);
            binding.projectCreator.setText("创建者：" + db.getCreatorName());
            binding.projectCreate.setVisibility(View.VISIBLE);
            binding.projectCreate.setText("创建时间：" + ConstantUtil.YMDHM.format(new Date(db.getCreateTime())));
            if (!TextUtils.isEmpty(db.getRemark())) {
                binding.projectRemarkTv.setText("备注：" + db.getRemark());
            }

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
                binding.projectMembers.setText(names);
                binding.projectMembersLl.setVisibility(View.VISIBLE);
            } else {
                binding.projectMembersLl.setVisibility(View.GONE);
            }

            binding.projectStart.setText(ConstantUtil.YMDHM.format(new Date(db.getStartTime())));
            binding.projectEnd.setText(ConstantUtil.YMDHM.format(new Date(db.getEndTime())));

            if (db.getForceFinish() == 1) {
                binding.projectFinish.setVisibility(View.VISIBLE);
                binding.projectFinish.setText("取消时间：" + ConstantUtil.YMDHM.format(new Date(db.getCloseTime())));
            } else if (db.getForceFinish() == 2) {
                binding.projectFinish.setVisibility(View.VISIBLE);
                binding.projectFinish.setText("终止时间：" + ConstantUtil.YMDHM.format(new Date(db.getCloseTime())));
            }

            binding.projectStatusLl.setVisibility(View.VISIBLE);
            binding.projectButton.setVisibility(View.GONE);
            if (db.getApproveResult() == 0) {
                binding.projectRemark.setVisibility(View.GONE);
                if (db.getForceFinish() == 1) {
                    setTextStutas("已取消", Color.argb(168, 255, 0, 0));
                } else {
                    setTextStutas("待审批", Color.argb(168, 0, 0, 255));
                    if (App.sp.getInt("userId", 0) == db.getSuperId()) {
                        binding.projectStatus.setVisibility(View.GONE);
                        binding.projectStatusRg.setVisibility(View.VISIBLE);
                        binding.projectStatus2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                binding.projectStatusCause.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                            }
                        });
                        binding.projectButton.setVisibility(View.VISIBLE);
                    } else if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                        binding.projectIsfinish.setVisibility(View.VISIBLE);
                        binding.projectIsfinish.setText("取消项目");
                        binding.projectIsfinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                binding.projectIsfinishCause.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                                binding.projectButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                                binding.projectIsfinishCause.setHint("取消原因");
                            }
                        });
                    }
                }
            } else if (db.getApproveResult() == 2) {
                setTextStutas("被拒绝", Color.argb(168, 255, 0, 255));
            } else if (db.getForceFinish() == 0) {
                binding.projectApproveTime.setVisibility(View.VISIBLE);
                binding.projectApproveTime.setText("审批时间：" + ConstantUtil.YMDHM.format(new Date(db.getApproveTime())));
                if (db.getStartTimeReal() > 0) {
                    binding.projectDesignTime.setVisibility(View.VISIBLE);
                    binding.projectDesignTime.setText("实际开始时间：" + ConstantUtil.YMDHM.format(new Date(db.getStartTimeReal())));
                }
                if (db.getStartTimeDevelop() > 0) {
                    binding.projectDevelopTime.setVisibility(View.VISIBLE);
                    binding.projectDevelopTime.setText("开始研发时间：" + ConstantUtil.YMDHM.format(new Date(db.getStartTimeDevelop())));
                }
                if (db.getStartTimeTest() > 0) {
                    binding.projectTestTime.setVisibility(View.VISIBLE);
                    binding.projectTestTime.setText("开始测试时间：" + ConstantUtil.YMDHM.format(new Date(db.getStartTimeTest())));
                }
                if (db.getEndTimeReal() > 0) {
                    binding.projectEndReal.setVisibility(View.VISIBLE);
                    binding.projectEndReal.setText("实际完成时间：" + ConstantUtil.YMDHM.format(new Date(db.getEndTimeReal())));
                }
                int status = db.getStatus();
                if (status == 0) {
                    setTextStutas("未开始", Color.argb(168, 0, 0, 255));
                    binding.projectStatusChange.setText("开始项目");
                } else if (status == 1) {
                    setTextStutas("设计中", Color.argb(168, 0, 255, 0));
                    binding.projectStatusChange.setText("开始研发");
                } else if (status == 2) {
                    setTextStutas("开发中", Color.argb(168, 0, 255, 0));
                    binding.projectStatusChange.setText("开始测试");
                } else if (status == 3) {
                    setTextStutas("测试中", Color.argb(168, 0, 255, 0));
                    binding.projectStatusChange.setText("完成项目");
                } else {
                    setTextStutas("已完成", Color.argb(168, 0, 0, 0));
                    binding.projectRemark.setVisibility(View.GONE);
                    binding.projectButton.setVisibility(View.GONE);
                    return;
                }
                if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                    binding.projectIsfinish.setVisibility(View.VISIBLE);
                    binding.projectIsfinish.setText("终止项目");
                    binding.projectIsfinishCause.setVisibility(View.VISIBLE);
                    binding.projectButton.setVisibility(View.VISIBLE);
                    binding.projectIsfinishCause.setHint("终止原因");
                    binding.projectStatusChange.setVisibility(View.VISIBLE);

                    binding.projectStatusChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            binding.projectIsfinish.setChecked(!isChecked);
                            if (isChecked) {
                                binding.projectIsfinishCause.setVisibility(View.GONE);
                                binding.projectRemark.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    binding.projectIsfinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            binding.projectStatusChange.setChecked(!isChecked);
                            if (isChecked) {
                                binding.projectIsfinishCause.setVisibility(View.VISIBLE);
                                binding.projectRemark.setVisibility(View.GONE);
                            }
                        }
                    });
                    binding.projectStatusChange.setChecked(true);
                } else {
                    binding.projectRemark.setVisibility(View.GONE);
                }
            } else if (db.getForceFinish() == 1) {
                setTextStutas("已取消", Color.argb(168, 255, 0, 0));
                binding.projectRemark.setVisibility(View.GONE);
            } else {
                setTextStutas("已终止", Color.argb(168, 255, 0, 0));
                binding.projectRemark.setVisibility(View.GONE);
            }
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
            if (App.sp.getInt("userId", 0) == db.getCreatorId()) {
                if (binding.projectIsfinish.isChecked()) {
                    fields.put("forceFinish", "" + (db.getApproveResult() == 0 ? 1 : 2));
                    fields.put("cause", binding.projectIsfinishCause.getText().toString().trim());
                } else if (binding.projectStatusChange.isChecked()) {
                    int status = db.getStatus();
                    if (status == 0) {
                        fields.put("status", "1");
                    } else if (status == 1) {
                        fields.put("status", "2");
                    } else if (status == 2) {
                        fields.put("status", "3");
                    } else if (status == 3) {
                        fields.put("status", "4");
                    }
                    if (!TextUtils.isEmpty(binding.projectRemark.getText().toString().trim())) {
                        fields.put("remark", binding.projectRemark.getText().toString().trim());
                    }
                } else {
                    App.showToast("请先选择操作方式");
                    return;
                }
            } else if (App.sp.getInt("userId", 0) == db.getSuperId()) {
                fields.put("approveResult", "" + (binding.projectStatus2.isChecked() ? 2 : 1));
                fields.put("refuseCause", binding.projectIsfinishCause.getText().toString().trim());
            }
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
