package com.niucong.punchcardclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.VacateActivity;
import com.niucong.punchcardclient.net.db.VacateRecordDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VacateRecordAdapter extends BaseQuickAdapter<VacateRecordDB, BaseViewHolder> {

    private Context context;
    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * @param layoutResId
     * @param dbs
     */
    public VacateRecordAdapter(Context context, int layoutResId, List<VacateRecordDB> dbs) {
        super(layoutResId, dbs);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final VacateRecordDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_vacate_num, (position + 1) + "");
        helper.setText(R.id.item_vacate_name, db.getName());
        helper.setText(R.id.item_vacate_type, "类型：" + (db.getType() == 1 ? "事假" : db.getType() == 2 ? "病假" :
                db.getType() == 3 ? "年假" : db.getType() == 4 ? "调休" : "其它"));
        Log.d("VacateRecordAdapter", "cause=" + db.getCause());
//        helper.setVisible(R.id.item_vacate_cause, !TextUtils.isEmpty(db.getCause()));
        helper.setText(R.id.item_vacate_cause, "原因：" + db.getCause());
        helper.setText(R.id.item_vacate_starttime, YMDHM.format(new Date(db.getStartTime())));
        helper.setText(R.id.item_vacate_endtime, YMDHM.format(new Date(db.getEndTime())));
        helper.setText(R.id.item_vacate_creattime, "提交时间：" + YMDHM.format(new Date(db.getCreateTime())));
        helper.setText(R.id.item_vacate_edittime, "批复时间：" + (db.getEditTime() > 0 ? YMDHM.format(new Date(db.getEditTime())) : ""));

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
                boolean isEdit = false;
                if (db.getType() == 3 && db.getSuperId() == 0) {
                    isEdit = true;
                }
                ((Activity) context).startActivityForResult(new Intent(context, VacateActivity.class)
                        .putExtra("VacateRecordDB", db), 1);
            }
        });

    }

    private void setTextStutas(BaseViewHolder helper, String status, int Color) {
        helper.setText(R.id.item_vacate_status, status);
        helper.setTextColor(R.id.item_vacate_status, Color);
    }
}
