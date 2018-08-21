package com.niucong.punchcardclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.niucong.punchcardclient.adapter.SignRecordAdapter;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.SignInListBean;
import com.niucong.punchcardclient.net.db.SignRecordDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignRecordListActivity extends BasicActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.signrecord_search)
    EditText signrecordSearch;
    @BindView(R.id.signrecord_rv)
    RecyclerView signrecordRv;
    @BindView(R.id.signrecord_srl)
    SwipeRefreshLayout signrecordSrl;

    private SignRecordAdapter adapter;
    private List<SignRecordDB> list = new ArrayList<>();

    private int allSize;
    private int offset = 0;
    private int pageSize = 10;
    String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signrecord_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        signrecordSearch.addTextChangedListener(new TextWatcher() {
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
        signrecordSrl.setOnRefreshListener(this);
        signrecordSrl.setColorSchemeColors(Color.rgb(47, 223, 189));
        adapter = new SignRecordAdapter(R.layout.item_signrecord, list);
        adapter.setOnLoadMoreListener(this, signrecordRv);
        signrecordRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        signrecordRv.setAdapter(adapter);
    }

    private void queryMembers() {

        Map<String, String> fields = new HashMap<>();
        fields.put("offset", "" + offset);
        fields.put("pageSize", "" + pageSize);
        if (!TextUtils.isEmpty(searchKey)) {
            fields.put("searchKey", searchKey);
        }
        addSubscription(getApi().signInList(fields), new ApiCallback<SignInListBean>() {
            @Override
            public void onSuccess(SignInListBean model) {
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
                        signrecordSrl.setRefreshing(false);
                        //禁止下拉刷新
                        signrecordSrl.setEnabled(true);
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
                signrecordSrl.setRefreshing(false);
                //禁止下拉刷新
                signrecordSrl.setEnabled(true);
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
        signrecordSrl.setEnabled(false);
        offset = list.size();
        queryMembers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_signrecord, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_date:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
