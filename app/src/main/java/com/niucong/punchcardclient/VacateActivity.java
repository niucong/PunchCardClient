package com.niucong.punchcardclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.databinding.ActivityVacateBinding;
import com.niucong.punchcardclient.dialog.DateSelectDialog;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.VacateBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class VacateActivity extends BasicActivity {

    ActivityVacateBinding binding;

    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
        final int type = binding.vataceLeave1.isChecked() ? 1 : binding.vataceLeave2.isChecked() ? 2 :
                binding.vataceLeave3.isChecked() ? 3 : binding.vataceLeave4.isChecked() ? 4 : 5;
        final String cause = binding.vataceCause.getText().toString();
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

            Map<String, String> fields = new HashMap<>();
            fields.put("type", "" + type);
            fields.put("cause", "" + cause);
            fields.put("start", "" + startDate);
            fields.put("end", "" + endDate);
            addSubscription(getApi().vacate(fields), new ApiCallback<VacateBean>() {
                @Override
                public void onSuccess(VacateBean model) {
                    if (model != null) {
                        App.showToast("" + model.getMsg());
                        if (model.getCode() == 1) {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
