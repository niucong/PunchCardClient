package com.niucong.punchcardclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.niucong.punchcardclient.adapter.VacateAdapter;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.VacateListBean;
import com.niucong.punchcardclient.net.db.VacateDB;
import com.niucong.selectdatetime.view.NiftyDialogBuilder;
import com.niucong.selectdatetime.view.wheel.DateTimeSelectView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VacateListActivity extends BasicActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.vacate_search)
    EditText vacateSearch;
    @BindView(R.id.vacate_rv)
    RecyclerView vacateRv;
    @BindView(R.id.vacate_srl)
    SwipeRefreshLayout vacateSrl;

    private VacateAdapter adapter;
    private List<VacateDB> list = new ArrayList<>();

    private int allSize;
    private int offset = 0;
    private int pageSize = 10;
    String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacate_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        vacateSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = s.toString();
                offset = 0;
                queryMembers();
            }
        });

        setAdapter();
        queryMembers();
    }

    private void setAdapter() {
        vacateSrl.setOnRefreshListener(this);
        vacateSrl.setColorSchemeColors(Color.rgb(47, 223, 189));
        adapter = new VacateAdapter(this, R.layout.item_vacate, list);
        adapter.setOnLoadMoreListener(this, vacateRv);
        vacateRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        vacateRv.setAdapter(adapter);
    }

    private void queryMembers() {
        Map<String, String> fields = new HashMap<>();
        fields.put("offset", "" + offset);
        fields.put("pageSize", "" + pageSize);
        if (!TextUtils.isEmpty(searchKey)) {
            fields.put("searchKey", searchKey);
        }
        addSubscription(getApi().vacateList(fields), new ApiCallback<VacateListBean>() {
            @Override
            public void onSuccess(VacateListBean model) {
                if (model != null) {
                    App.showToast("" + model.getMsg());
                    if (model.getCode() == 1) {
                        if (offset == 0) {
                            list.clear();
                            allSize = model.getAllSize();
                        }
                        list.addAll(model.getList());
                        adapter.notifyDataSetChanged();
                        if (allSize == list.size()) {
                            adapter.loadMoreEnd();
                        } else {
                            adapter.loadMoreComplete();
                        }
                        //取消下拉刷新动画
                        vacateSrl.setRefreshing(false);
                        //禁止下拉刷新
                        vacateSrl.setEnabled(true);
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
                //取消下拉刷新动画
                vacateSrl.setRefreshing(false);
                //禁止下拉刷新
                vacateSrl.setEnabled(true);
            }
        });
    }

    @Override
    public void onRefresh() {
        adapter.setEnableLoadMore(false);
        offset = 0;
        queryMembers();
    }

    @Override
    public void onLoadMoreRequested() {
        vacateSrl.setEnabled(false);
        offset = list.size();
        queryMembers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                adapter.setEnableLoadMore(false);
                offset = 0;
                queryMembers();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_vacate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_add:
                startActivityForResult(new Intent(VacateListActivity.this, VacateActivity.class), 1);
                break;
            case R.id.action_date:
                showSubmitDia();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Date startDate, endDate;

    /**
     * 选择日期对话框
     */
    private void showSubmitDia() {
        final NiftyDialogBuilder submitDia = NiftyDialogBuilder.getInstance(this);
        View selectDateView = LayoutInflater.from(this).inflate(R.layout.dialog_select_date, null);
        final DateTimeSelectView ds = (DateTimeSelectView) selectDateView.findViewById(R.id.date_start);
        final DateTimeSelectView de = (DateTimeSelectView) selectDateView.findViewById(R.id.date_end);

        final SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Calendar c = Calendar.getInstance();
        try {
            startDate = ymdhm.parse(ymdhm.format(new Date()));// 当日00：00：00
        } catch (ParseException e) {
            e.printStackTrace();
        }
        endDate = new Date();

        submitDia.withTitle("选择查询日期");
        submitDia.withButton1Text("取消", 0).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDia.dismiss();
            }
        });
        submitDia.withButton2Text("确定", 0).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startDate = ymdhm.parse(ds.getDate());
//                    if (ymd.format(new Date()).equals(de.getDate())) {// 结束日期是今天
//                        endDate = new Date();// 当前时间
//                    } else {
//                        endDate = new Date(ymd.parse(de.getDate()).getTime() + 1000 * 60 * 60 * 24 - 1);// 当日23：59：59
//                    }
                    endDate = ymdhm.parse(de.getDate());
                    if (endDate.before(startDate)) {
                        App.showToast("开始日期不能大于结束日期");
                    } else {
                        queryMembers();
                        submitDia.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        submitDia.setCustomView(selectDateView, this);// "请选择查询日期"
        submitDia.withMessage(null).withDuration(400);
        submitDia.isCancelable(false);
        submitDia.show();
    }

}
