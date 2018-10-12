package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.ProjectDB;

import java.util.List;

public class ProjectListBean extends BasicBean {

    private int allSize;
    private List<ProjectDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<ProjectDB> getList() {
        return list;
    }

    public void setList(List<ProjectDB> list) {
        this.list = list;
    }
}
