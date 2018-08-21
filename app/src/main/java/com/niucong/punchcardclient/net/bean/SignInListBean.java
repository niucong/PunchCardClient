package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.SignRecordDB;

import java.util.List;

public class SignInListBean extends BasicBean {

    private int allSize;
    private List<SignRecordDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<SignRecordDB> getList() {
        return list;
    }

    public void setList(List<SignRecordDB> list) {
        this.list = list;
    }
}
