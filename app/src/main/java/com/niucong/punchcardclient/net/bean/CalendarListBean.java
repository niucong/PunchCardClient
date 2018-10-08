package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.CalendarDB;

import java.util.List;

public class CalendarListBean extends BasicBean {

    private int allSize;
    private List<CalendarDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<CalendarDB> getList() {
        return list;
    }

    public void setList(List<CalendarDB> list) {
        this.list = list;
    }
}
