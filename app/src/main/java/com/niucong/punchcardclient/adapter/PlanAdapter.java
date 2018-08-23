package com.niucong.punchcardclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.PlanActivity;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.net.db.MemberDB;
import com.niucong.punchcardclient.net.db.PlanDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlanAdapter extends BaseQuickAdapter<PlanDB, BaseViewHolder> {

    private Context context;
    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * @param layoutResId
     * @param dbs
     */
    public PlanAdapter(Context context, int layoutResId, List<PlanDB> dbs) {
        super(layoutResId, dbs);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PlanDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_plan_num, (position + 1) + "");
        helper.setText(R.id.item_plan_name, db.getName());
        helper.setText(R.id.item_plan_starttime, "起：" + YMDHM.format(new Date(db.getStartTime())));
        helper.setText(R.id.item_plan_endtime, "止：" + YMDHM.format(new Date(db.getEndTime())));
        helper.setText(R.id.item_plan_creator, "创建者：" + db.getCreaterName());
        helper.setText(R.id.item_plan_creattime, YMDHM.format(new Date(db.getCreateTime())));

        if (db.getEditTime() > 0) {
            helper.setText(R.id.item_plan_edittime, "修改时间：" + YMDHM.format(new Date(db.getEditTime())));
            helper.setGone(R.id.item_plan_edittime, true);
        } else {
            helper.setGone(R.id.item_plan_edittime, false);
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
            helper.setText(R.id.item_plan_owners, "相关人员：" + names);
            helper.setGone(R.id.item_plan_owners, true);
        } else {
            helper.setGone(R.id.item_plan_owners, false);
        }

        if (db.getForceFinish() == 0) {
            if (db.getStartTime() > System.currentTimeMillis()) {
                setTextStutas(helper, "未开始", Color.argb(168, 0, 0, 255));
            } else if (db.getEndTime() > System.currentTimeMillis()) {
                setTextStutas(helper, "进行中", Color.argb(168, 0, 255, 0));
            } else {
                setTextStutas(helper, "已结束", Color.argb(168, 255, 255, 255));
            }
        } else if (db.getForceFinish() == 1) {
            setTextStutas(helper, "已取消", Color.argb(168, 255, 0, 0));
        } else {
            setTextStutas(helper, "已终止", Color.argb(168, 255, 0, 0));
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).startActivityForResult(new Intent(context, PlanActivity.class)
                        .putExtra("PlanDB", db), 1);
            }
        });

    }

    private void setTextStutas(BaseViewHolder helper, String status, int Color) {
        helper.setText(R.id.item_plan_status, status);
        helper.setTextColor(R.id.item_plan_status, Color);
    }
}
