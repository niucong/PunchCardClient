package com.niucong.punchcardclient.net.bean;

public class LoginBean extends BasicBean {

    private int memberId;
    private int type;// 身份类型：1主任、2老师、3学生

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
