package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.PlanDB;

import java.util.List;

public class PlanListBean extends BasicBean {

    private int allSize;
    private List<PlanDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<PlanDB> getList() {
        return list;
    }

    public void setList(List<PlanDB> list) {
        this.list = list;
    }
}
