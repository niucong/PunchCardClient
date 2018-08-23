package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.SignDB;

import java.util.List;

public class SignListBean extends BasicBean {

    private int allSize;
    private List<SignDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<SignDB> getList() {
        return list;
    }

    public void setList(List<SignDB> list) {
        this.list = list;
    }
}
