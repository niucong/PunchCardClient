package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.VacateDB;

import java.util.List;

public class VacateListBean extends BasicBean {

    private int allSize;
    private List<VacateDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<VacateDB> getList() {
        return list;
    }

    public void setList(List<VacateDB> list) {
        this.list = list;
    }
}
