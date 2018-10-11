package com.niucong.punchcardclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.VacateActivity;
import com.niucong.punchcardclient.net.db.VacateDB;
import com.niucong.punchcardclient.util.ConstantUtil;

import java.util.Date;
import java.util.List;

public class VacateAdapter extends BaseQuickAdapter<VacateDB, BaseViewHolder> {

    private Context context;

    /**
     * @param layoutResId
     * @param dbs
     */
    public VacateAdapter(Context context, int layoutResId, List<VacateDB> dbs) {
        super(layoutResId, dbs);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final VacateDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_vacate_num, (position + 1) + "");
        helper.setText(R.id.item_vacate_name, db.getName());
        helper.setText(R.id.item_vacate_type, "类型：" + (db.getType() == 1 ? "事假" : db.getType() == 2 ? "病假" :
                db.getType() == 3 ? "年假" : db.getType() == 4 ? "调休" : "其它"));
        helper.setGone(R.id.item_vacate_cause, !TextUtils.isEmpty(db.getCause()));
        helper.setText(R.id.item_vacate_cause, "原因：" + db.getCause());
        helper.setText(R.id.item_vacate_starttime, "起：" + ConstantUtil.YMDHM.format(new Date(db.getStartTime())));
        helper.setText(R.id.item_vacate_endtime, "止：" + ConstantUtil.YMDHM.format(new Date(db.getEndTime())));
        helper.setText(R.id.item_vacate_creattime, "提交时间：" + ConstantUtil.YMDHM.format(new Date(db.getCreateTime())));
        if (db.getEditTime() > 0) {
            helper.setText(R.id.item_vacate_edittime, "批复时间：" + ConstantUtil.YMDHM.format(new Date(db.getEditTime())));
            helper.setGone(R.id.item_vacate_edittime, true);
        } else {
            helper.setGone(R.id.item_vacate_edittime, false);
        }


        if (db.getApproveResult() == 0) {
            setTextStutas(helper, "待批复", Color.argb(168, 0, 0, 255));
        } else if (db.getApproveResult() == 1) {
            helper.setText(R.id.item_vacate_status, "同意");
            setTextStutas(helper, "同意", Color.argb(168, 0, 255, 0));
        } else {
            setTextStutas(helper, "不同意", Color.argb(168, 255, 0, 0));
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).startActivityForResult(new Intent(context, VacateActivity.class)
                        .putExtra("VacateDB", db), 1);
            }
        });

    }

    private void setTextStutas(BaseViewHolder helper, String status, int Color) {
        helper.setText(R.id.item_vacate_status, status);
        helper.setTextColor(R.id.item_vacate_status, Color);
    }
}
