package com.niucong.punchcardclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.niucong.punchcardclient.net.bean.ParcelableMap;
import com.niucong.punchcardclient.net.db.ClassTimeDB;
import com.niucong.punchcardclient.util.ConstantUtil;

import org.litepal.LitePal;

import java.util.List;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectClassTimeActivity extends BasicActivity {

    @BindView(R.id.select_class_time_monday)
    GridView selectClassTimeMonday;
    @BindView(R.id.select_class_time_tuesday)
    GridView selectClassTimeTuesday;
    @BindView(R.id.select_class_time_wednesday)
    GridView selectClassTimeWednesday;
    @BindView(R.id.select_class_time_thursday)
    GridView selectClassTimeThursday;
    @BindView(R.id.select_class_time_firday)
    GridView selectClassTimeFirday;
    @BindView(R.id.select_class_time_saturday)
    GridView selectClassTimeSaturday;
    @BindView(R.id.select_class_time_sunday)
    GridView selectClassTimeSunday;

    private long courseId;// 课程ID
    private Map<Integer, ClassTimeDB> selectMap;
    private ParcelableMap sMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class_time);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        courseId = getIntent().getLongExtra("courseId", 0);
        sMap = getIntent().getParcelableExtra("sMap");
        selectMap = sMap.getMap();

        selectClassTimeMonday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[0]).find(ClassTimeDB.class)));
        selectClassTimeTuesday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[1]).find(ClassTimeDB.class)));
        selectClassTimeWednesday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[2]).find(ClassTimeDB.class)));
        selectClassTimeThursday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[3]).find(ClassTimeDB.class)));
        selectClassTimeFirday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[4]).find(ClassTimeDB.class)));
        selectClassTimeSaturday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[5]).find(ClassTimeDB.class)));
        selectClassTimeSunday.setAdapter(new TextAdapter(this, LitePal.where("weekDay = ?", ConstantUtil.WEEKS[6]).find(ClassTimeDB.class)));
    }

    class TextAdapter extends BaseAdapter {

        private Context context;
        private List<ClassTimeDB> list;

        public TextAdapter(Context context, List<ClassTimeDB> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            //如果convertView为空就创建一个View，否则使用getTag缓存的View
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_select_class_time, null);
                holder.text = (TextView) convertView.findViewById(R.id.item_select_class_time);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final ClassTimeDB timeDB = list.get(position);
            holder.text.setText(timeDB.getSectionName());
            if (timeDB.getCourseId() > 0 && courseId != timeDB.getCourseId()) {
                holder.text.setBackground(context.getResources().getDrawable(R.drawable.gray_circle));
                convertView.setOnClickListener(null);
            } else {
                if (selectMap != null && selectMap.containsKey(timeDB.getId())) {
                    holder.text.setBackground(context.getResources().getDrawable(R.drawable.red_circle));
                } else {
                    holder.text.setBackground(context.getResources().getDrawable(R.drawable.green_circle));
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectMap.containsKey(timeDB.getId())) {
                            selectMap.remove(timeDB.getId());
                        } else {
                            selectMap.put(timeDB.getId(), timeDB);
                        }
                        notifyDataSetChanged();
                    }
                });
            }
            return convertView;
        }

        class Holder {
            TextView text;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.select_class_time_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_class_time_button:
                sMap.setMap(selectMap);
                setResult(RESULT_OK, new Intent().putExtra("sMap", sMap));
                finish();
                break;
        }
    }

}
