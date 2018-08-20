package com.niucong.punchcardclient.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.niucong.punchcardclient.R;
import com.niucong.punchcardclient.db.SignRecordDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignRecordAdapter extends BaseQuickAdapter<SignRecordDB, BaseViewHolder> {

    SimpleDateFormat YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * @param layoutResId
     * @param dbs
     */
    public SignRecordAdapter(int layoutResId, List<SignRecordDB> dbs) {
        super(layoutResId, dbs);
    }

    @Override
    protected void convert(BaseViewHolder helper, final SignRecordDB db) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.item_signrecord_num, (position + 1) + "");
        helper.setText(R.id.item_signrecord_starttime, YMDHMS.format(new Date(db.getStartTime())));
        helper.setText(R.id.item_signrecord_endtime, db.getEndTime() > 0 ? YMDHMS.format(new Date(db.getEndTime())) : "-");
    }
}