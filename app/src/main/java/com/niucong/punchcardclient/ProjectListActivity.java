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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.niucong.punchcardclient.adapter.PlanAdapter;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;
import com.niucong.punchcardclient.net.bean.PlanListBean;
import com.niucong.punchcardclient.net.db.PlanDB;
import com.niucong.selectdatetime.view.NiftyDialogBuilder;
import com.niucong.selectdatetime.view.wheel.DateSelectView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectListActivity extends BasicActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.plan_search)
    EditText planSearch;
    @BindView(R.id.plan_rv)
    RecyclerView planRv;
    @BindView(R.id.plan_srl)
    SwipeRefreshLayout planSrl;

    private PlanAdapter adapter;
    private List<PlanDB> list = new ArrayList<>();

    private int allSize;
    private int offset = 0;
    private int pageSize = 10;
    String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        planSearch.addTextChangedListener(new TextWatcher() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicBean bean) {
        Log.d("PlanListActivity", "code=" + bean.getCode());
        if (bean.getCode() == 1) {
            onRefresh();
        }
    }

    private void setAdapter() {
        planSrl.setOnRefreshListener(this);
        planSrl.setColorSchemeColors(Color.rgb(47, 223, 189));
        adapter = new PlanAdapter(this, R.layout.item_plan, list);
        adapter.setOnLoadMoreListener(this, planRv);
        planRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        planRv.setAdapter(adapter);
    }

    private void queryMembers() {
        Map<String, String> fields = new HashMap<>();
        fields.put("offset", "" + offset);
        fields.put("pageSize", "" + pageSize);
        if (!TextUtils.isEmpty(searchKey)) {
            fields.put("searchKey", searchKey);
        }
        if (startDate != null && endDate != null) {
            fields.put("startTime", startDate.getTime() + "");
            fields.put("endTime", endDate.getTime() + 24 * 60 * 60 * 1000 + "");
        }
        Log.d("PlanListActivity", "fields=" + fields.toString());
        addSubscription(getApi().planList(fields), new ApiCallback<PlanListBean>() {
            @Override
            public void onSuccess(PlanListBean model) {
                if (model != null) {
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
                        planSrl.setRefreshing(false);
                        //禁止下拉刷新
                        planSrl.setEnabled(true);
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
                //取消下拉刷新动画
                planSrl.setRefreshing(false);
                //禁止下拉刷新
                planSrl.setEnabled(true);
            }
        });
    }

    @Override
    public void onRefresh() {
        adapter.setEnableLoadMore(false);
        offset = 0;
        startDate = null;
        endDate = null;
        queryMembers();
    }

    @Override
    public void onLoadMoreRequested() {
        planSrl.setEnabled(false);
        offset = list.size();
        queryMembers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                onRefresh();
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
                startActivityForResult(new Intent(ProjectListActivity.this, ProjectActivity.class), 1);
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
        final DateSelectView ds = (DateSelectView) selectDateView.findViewById(R.id.date_start);
        final DateSelectView de = (DateSelectView) selectDateView.findViewById(R.id.date_end);

        final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = YMD.parse(YMD.format(new Date()));// 当日00：00：00
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
                    startDate = YMD.parse(ds.getDate());
//                    if (ymd.format(new Date()).equals(de.getDate())) {// 结束日期是今天
//                        endDate = new Date();// 当前时间
//                    } else {
//                        endDate = new Date(ymd.parse(de.getDate()).getTime() + 1000 * 60 * 60 * 24 - 1);// 当日23：59：59
//                    }
                    endDate = YMD.parse(de.getDate());
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
