package com.niucong.punchcardclient;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityVacateBinding;
import com.niucong.punchcardclient.dialog.DateSelectDialog;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.VacateBean;
import com.niucong.punchcardclient.net.db.VacateRecordDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VacateActivity extends BasicActivity {

    ActivityVacateBinding binding;

    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private VacateRecordDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vacate);
        binding.setHandlers(new VacateClickHandlers());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (App.sp.getInt("type", 0) == 3) {
            binding.vataceLeave3.setVisibility(View.GONE);
            binding.vataceLeave4.setVisibility(View.GONE);
            binding.vataceLeave5.setVisibility(View.GONE);
        }

        db = getIntent().getParcelableExtra("VacateRecordDB");
        if (db != null) {
            binding.vataceType.setVisibility(View.GONE);
            binding.vataceTypeTv.setVisibility(View.VISIBLE);
            binding.vataceTypeTv.setText(db.getType() == 1 ? "事假" : db.getType() == 2 ? "病假" :
                    db.getType() == 3 ? "年假" : db.getType() == 4 ? "调休" : "其它");
            binding.vataceCauseTv.setText(db.getCause());
            binding.vataceCauseTv.setVisibility(View.VISIBLE);
            binding.vataceCause.setVisibility(View.GONE);
            binding.vataceStart.setText(YMDHM.format(new Date(db.getStartTime())));
            binding.vataceEnd.setText(YMDHM.format(new Date(db.getEndTime())));

            binding.vataceStart.setTextColor(Color.GRAY);
            binding.vataceEnd.setTextColor(Color.GRAY);
            binding.vataceStart.setEnabled(false);
            binding.vataceEnd.setEnabled(false);

            binding.vataceStatusLl.setVisibility(View.VISIBLE);
            Log.d("VacateActivity", "userId=" + App.sp.getInt("userId", 0) + ",MemberId=" + db.getMemberId() + ",SuperId=" + db.getSuperId());
            if (App.sp.getInt("userId", 0) == db.getSuperId()) {
                binding.vataceStatus.setVisibility(View.GONE);
                binding.vataceStatusRg.setVisibility(View.VISIBLE);
                binding.vataceStatus2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        binding.vataceStatusCause.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                binding.vataceStatus.setVisibility(View.VISIBLE);
                binding.vataceStatusRg.setVisibility(View.GONE);
                binding.vataceStatus.setText(db.getApproveResult() == 0 ? "待批复" :
                        db.getApproveResult() == 1 ? "同意" : "不同意");
            }
        }
    }

    public class VacateClickHandlers {
        public void onClickName(View v) {
            switch (v.getId()) {
                case R.id.vatace_start:
                    DateSelectDialog.selectDateTime(VacateActivity.this, binding.vataceStart);
                    break;
                case R.id.vatace_end:
                    DateSelectDialog.selectDateTime(VacateActivity.this, binding.vataceEnd);
                    break;
                case R.id.vatace_button:
                    submitVacate();
                    break;
            }
        }
    }

    private void submitVacate() {
        Map<String, String> fields = new HashMap<>();
        if (db == null) {
            final int type = binding.vataceLeave1.isChecked() ? 1 : binding.vataceLeave2.isChecked() ? 2 :
                    binding.vataceLeave3.isChecked() ? 3 : binding.vataceLeave4.isChecked() ? 4 : 5;
            final String cause = binding.vataceCause.getText().toString().trim();
            String start = binding.vataceStart.getText().toString().trim();
            String end = binding.vataceEnd.getText().toString().trim();
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

                fields.put("type", "" + type);
                fields.put("cause", cause);
                fields.put("start", "" + startDate);
                fields.put("end", "" + endDate);
            } catch (ParseException e) {
                e.printStackTrace();
                App.showToast("时间有误");
                return;
            }
        } else {
            fields.put("serverId", "" + db.getId());
            fields.put("approveResult", "" + (binding.vataceStatus2.isChecked() ? 2 : 1));
            fields.put("refuseCause", binding.vataceStatusCause.getText().toString().trim());
        }
        addSubscription(getApi().vacate(fields), new ApiCallback<VacateBean>() {
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
