package com.niucong.punchcardclient.db;

import org.litepal.crud.DataSupport;

/**
 * 打卡记录
 */
public class SignRecordDB extends DataSupport {

    private int id;// 唯一主键
    private int clientId;// 客户端生成的Id
    private int memberId;// 实验室人员Id
    private long time;// 打卡时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
