package com.niucong.punchcardclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.MemberActivity;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.net.db.MemberDB;

import java.util.List;

public class MemberAdapter extends BaseQuickAdapter<MemberDB, BaseViewHolder> {

    private Context context;

    /**
     * @param context
     * @param layoutResId
     * @param dbs
     */
    public MemberAdapter(Context context, int layoutResId, List<MemberDB> dbs) {
        super(layoutResId, dbs);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final MemberDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_member_num, (position + 1) + "");
        helper.setText(R.id.item_member_name, db.getName());
        helper.setText(R.id.item_member_type, db.getType() == 1 ? "主任" : db.getType() == 2 ? "老师" : "学生");
        helper.setText(R.id.item_member_number, db.getNumber());
        helper.setText(R.id.item_member_phone, db.getPhone());
        helper.setText(R.id.item_member_password, db.getPassword());
        helper.setText(R.id.item_member_mac, db.getMAC());

        if (0 == db.getIsDelete()) {
            if (db.getType() != 1 && db.getSuperId() == 0) {
                helper.setText(R.id.item_member_status, "待编辑");
            } else {
                helper.setText(R.id.item_member_status, "正常");
            }
        } else {
            helper.setText(R.id.item_member_status, "停用");
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEdit = false;
                if (db.getType() == 3 && db.getSuperId() == 0) {
                    isEdit = true;
                }
                ((Activity) context).startActivityForResult(new Intent(context, MemberActivity.class)
                        .putExtra("MemberDB", db).putExtra("position", position)
                        .putExtra("isEdit", isEdit), 1);
            }
        });

        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((Activity) context).startActivityForResult(new Intent(context, MemberActivity.class)
                        .putExtra("MemberDB", db).putExtra("position", position)
                        .putExtra("isEdit", true), 1);
                return false;
            }
        });
    }
}
