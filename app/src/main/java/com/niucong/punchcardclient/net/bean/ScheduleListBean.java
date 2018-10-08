package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.ScheduleDB;

import java.util.List;

public class ScheduleListBean extends BasicBean {

    private int allSize;
    private List<ScheduleDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<ScheduleDB> getList() {
        return list;
    }

    public void setList(List<ScheduleDB> list) {
        this.list = list;
    }
}
