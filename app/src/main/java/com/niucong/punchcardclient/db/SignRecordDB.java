package com.niucong.punchcardclient.db;

import org.litepal.crud.DataSupport;

/**
 * 打卡记录
 */
public class SignRecordDB extends DataSupport {

    private long id;// 唯一主键
    private long serverId;// 服务端生成的Id
    private long startTime;
    private long endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
