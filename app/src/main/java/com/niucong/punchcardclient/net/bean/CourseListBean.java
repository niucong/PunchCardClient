package com.niucong.punchcardclient.net.bean;

import com.niucong.punchcardclient.net.db.CourseDB;

import java.util.List;

public class CourseListBean extends BasicBean {

    private int allSize;
    private List<CourseDB> list;

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    public List<CourseDB> getList() {
        return list;
    }

    public void setList(List<CourseDB> list) {
        this.list = list;
    }
}
