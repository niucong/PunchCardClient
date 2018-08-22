package com.niucong.punchcardclient.net.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 请假记录
 */
public class VacateRecordDB implements Parcelable {

    private long id;// 唯一主键
    private int memberId;// 请假者Id
    private String name;// 请假者名称
    private int superId;// 上级id
    private int type;// 请假类型：1事假、2病假、3年假、4调休、5其他（3、4、5只有老师有）
    private String cause;// 请假原因
    private long createTime;// 请假时间
    private long startTime;// 请假开始时间
    private long endTime;// 请假结束时间
    private long editTime;// 批复时间
    private int approveResult;// 审批结果：0待批复、1同意、2不同意

    private boolean isUpPush;// 是否推送给上级
    private boolean isDownPush;// 是否推送反馈给下级
    private long lastPushTime;// 最后一次推送时间
    private int number;// 推送次数

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuperId() {
        return superId;
    }

    public void setSuperId(int superId) {
        this.superId = superId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCause() {
        if (cause == null) {
            cause = "";
        }
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public boolean isUpPush() {
        return isUpPush;
    }

    public void setUpPush(boolean upPush) {
        isUpPush = upPush;
    }

    public int getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(int approveResult) {
        this.approveResult = approveResult;
    }

    public boolean isDownPush() {
        return isDownPush;
    }

    public void setDownPush(boolean downPush) {
        isDownPush = downPush;
    }

    public long getLastPushTime() {
        return lastPushTime;
    }

    public void setLastPushTime(long lastPushTime) {
        this.lastPushTime = lastPushTime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.memberId);
        dest.writeString(this.name);
        dest.writeInt(this.superId);
        dest.writeInt(this.type);
        dest.writeString(this.cause);
        dest.writeLong(this.createTime);
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
        dest.writeLong(this.editTime);
        dest.writeInt(this.approveResult);
        dest.writeByte(this.isUpPush ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDownPush ? (byte) 1 : (byte) 0);
        dest.writeLong(this.lastPushTime);
        dest.writeInt(this.number);
    }

    public VacateRecordDB() {
    }

    protected VacateRecordDB(Parcel in) {
        this.id = in.readLong();
        this.memberId = in.readInt();
        this.name = in.readString();
        this.superId = in.readInt();
        this.type = in.readInt();
        this.cause = in.readString();
        this.createTime = in.readLong();
        this.startTime = in.readLong();
        this.endTime = in.readLong();
        this.editTime = in.readLong();
        this.approveResult = in.readInt();
        this.isUpPush = in.readByte() != 0;
        this.isDownPush = in.readByte() != 0;
        this.lastPushTime = in.readLong();
        this.number = in.readInt();
    }

    public static final Creator<VacateRecordDB> CREATOR = new Creator<VacateRecordDB>() {
        @Override
        public VacateRecordDB createFromParcel(Parcel source) {
            return new VacateRecordDB(source);
        }

        @Override
        public VacateRecordDB[] newArray(int size) {
            return new VacateRecordDB[size];
        }
    };
}
