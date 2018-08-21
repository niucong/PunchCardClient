package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.VacateRecordDB;

import java.util.List;

public class VacateListBean extends BasicBean {

    private int allSize;
    private List<VacateRecordDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<VacateRecordDB> getList() {
        return list;
    }

    public void setList(List<VacateRecordDB> list) {
        this.list = list;
    }
}
