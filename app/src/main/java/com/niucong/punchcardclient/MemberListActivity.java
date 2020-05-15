package com.niucong.punchcardclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.niucong.punchcardclient.adapter.MemberAdapter;
import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.db.MemberDB;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberListActivity extends AppCompatActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.member_search)
    EditText memberSearch;
    @BindView(R.id.member_rv)
    RecyclerView memberRv;
    @BindView(R.id.member_srl)
    SwipeRefreshLayout memberSrl;

    private MemberAdapter adapter;
    private List<MemberDB> list = new ArrayList<>();

    private int allSize;
    private int offset = 0;
    private int pageSize = 10;
    private String searchKey;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        userId = "" + App.sp.getInt("userId", 0);

        memberSearch.addTextChangedListener(new TextWatcher() {
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
        memberSrl.setOnRefreshListener(this);
        memberSrl.setColorSchemeColors(Color.rgb(47, 223, 189));
        adapter = new MemberAdapter(this, R.layout.item_member, list);
        adapter.setOnLoadMoreListener(this, memberRv);
        memberRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        memberRv.setAdapter(adapter);
    }

    private void queryMembers() {
//        if (TextUtils.isEmpty(searchKey)) {
//            if (offset == 0) {
//                list.clear();
//                allSize = DataSupport.where("memberId = ?", userId).count(MemberDB.class);
//            }
//            list.addAll(DataSupport.where("memberId = ?", userId).offset(offset).limit(pageSize).find(MemberDB.class));
//        } else {
//            if (offset == 0) {
//                list.clear();
//                allSize = DataSupport.where("memberId = ? and number = ? or name = ? or phone = ?",
//                        userId, searchKey, searchKey, searchKey).count(MemberDB.class);
//            }
//            list.addAll(DataSupport.where("memberId = ? and number = ? or name = ? or phone = ?",
//                    userId, searchKey, searchKey, searchKey).offset(offset).limit(pageSize).find(MemberDB.class));
//        }
        Log.d("MemberListActivity", "queryMembers " + list.size() + "/" + allSize);
//        setAdapter();
        adapter.notifyDataSetChanged();
        if (allSize == list.size()) {
            adapter.loadMoreEnd();
        } else {
            adapter.loadMoreComplete();
        }
        //取消下拉刷新动画
        memberSrl.setRefreshing(false);
        //禁止下拉刷新
        memberSrl.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        adapter.setEnableLoadMore(false);
        offset = 0;
        queryMembers();
    }

    @Override
    public void onLoadMoreRequested() {
        memberSrl.setEnabled(false);
        offset = list.size();
        queryMembers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_member, menu);
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
                startActivityForResult(new Intent(MemberListActivity.this, MemberActivity.class), 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
