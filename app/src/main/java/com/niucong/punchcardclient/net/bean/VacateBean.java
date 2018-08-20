package com.niucong.punchcardclient.net.bean;

public class VacateBean extends BasicBean {

    private long serverId;
    private long createTime;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
