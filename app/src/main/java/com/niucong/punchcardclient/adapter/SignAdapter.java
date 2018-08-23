package com.niucong.punchcardclient.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.net.db.SignDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignAdapter extends BaseQuickAdapter<SignDB, BaseViewHolder> {

    SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * @param layoutResId
     * @param dbs
     */
    public SignAdapter(int layoutResId, List<SignDB> dbs) {
        super(layoutResId, dbs);
    }

    @Override
    protected void convert(BaseViewHolder helper, final SignDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_sign_num, (position + 1) + "");
        helper.setText(R.id.item_sign_name, db.getName());
        helper.setText(R.id.item_sign_starttime, YMDHM.format(new Date(db.getStartTime())));
        helper.setText(R.id.item_sign_endtime, db.getEndTime() > 0 ? YMDHM.format(new Date(db.getEndTime())) : "-");
    }
}
