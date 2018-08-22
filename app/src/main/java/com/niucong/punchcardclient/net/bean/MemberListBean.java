package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.MemberDB;

import java.util.List;

public class MemberListBean extends BasicBean {

    private List<MemberDB> list;

    public List<MemberDB> getList() {
        return list;
    }

    public void setList(List<MemberDB> list) {
        this.list = list;
    }
}
